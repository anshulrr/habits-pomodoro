# Non-Functional Requirements — Habits Pomodoro

This is a personal/side project serving a small number of real users (owner + accountability partners), not a product with formal SLAs. The bar set here is deliberately proportionate: enough rigor that a solo maintainer isn't surprised by data loss, a security hole, or an unreadable production incident — not enterprise-grade infrastructure for its own sake.

Covers four areas: **Security**, **Reliability & data integrity**, **Observability**, and **Performance, testability & maintainability**. Each section states the current state (grounded in the actual code, not aspiration), a target bar sized to this project, and the gap between them. See [ARCHITECTURE.md](ARCHITECTURE.md) and [features/](features/) for the functional picture these NFRs sit on top of.

A consolidated, priority-ordered backlog is at the [bottom](#prioritized-backlog) — that's the list to work through, one item at a time, the way we did with Tasks' known gaps.

---

## 1. Security

### Current state
- Auth is Firebase-verified JWT (`JwtAuthenticationFilter`); every endpoint requires authentication except `/manage/**` and CORS preflight. No endpoint is unintentionally open.
- Authorization is entirely hand-written per-query (`findUserXById(userId, id)` style) — there is no method-level `@PreAuthorize` except one admin check on `/users`. Correct today, but every new query is a fresh chance to leak another user's data.
- **Known hole:** `POST /accountability-partners` lets any authenticated user attach any other existing account by email with no consent step, and distinguishes `404` (no such email) from `409` (already linked) — a user-enumeration vector on top of a real privacy problem (see [Accountability Partners](features/07-accountability-partners.md#known-gaps)).
- Input validation is inconsistent: `TaskController` has 6 `@RequestBody` endpoints but only 2 use `@Valid`; `CommentController` has 7 and only 1 does. The DTOs that do have Bean Validation annotations only enforce them where `@Valid` is actually present on the controller method.
- Secrets handling is fine: `firebaseServiceAccountKey.json` and `application.properties` are gitignored; only a blank template (`application-copy.properties`) is committed.
- CORS is a single fixed origin from a property — reasonable for one frontend, no per-environment flexibility.
- No rate limiting anywhere (login attempts, API calls, accountability-partner probing) — nothing in `pom.xml` (no Bucket4j/resilience4j), nothing at the reverse-proxy layer implied by the code.
- Dependency freshness: Spring Boot 3.0.1 (Jan 2023, EOL — no security patches since), `firebase-admin` 9.2.0, `springdoc-openapi` 2.0.0 are all several versions behind current.
- Frontend: JWT refresh failures and malformed-token parsing are unhandled in the axios interceptor (`AuthContext.jsx`) — not a data-exposure risk today, but a failure mode that could leave a stale token in use.

### Target bar for this project
- No unauthenticated data access; every cross-user read is denied by default, not by convention.
- Every mutating endpoint validates its input — a malformed request gets a 400 with a clear reason, never a 500 or a silently-wrong write.
- No enumeration vectors on user-identifying data (email existence, account presence).
- Dependencies patched against known CVEs — doesn't need to track latest, needs to not be *actively* unpatched.
- Basic abuse resistance: a login-brute-force or accountability-partner-probing script shouldn't get unlimited free attempts.

### Gap → backlog
1. **P0** — Accountability-partner invite/accept flow, closing both the consent gap and the enumeration vector.
2. **P0** — Bump Spring Boot off the EOL 3.0.x line (this also unblocks patched transitive dependencies).
3. **P1** — Add `@Valid` to every `@RequestBody` endpoint in `TaskController` and `CommentController` (the two worst offenders); audit the rest.
4. **P1** — Basic rate limiting on `/accountability-partners` (POST) and the login/signup path.
5. **P2** — Fix the frontend JWT-refresh-failure and malformed-token handling (see [Auth & Settings gaps](features/08-auth-and-settings.md#known-gaps)).
6. **P2** — Bump `firebase-admin` and `springdoc-openapi` to current minor versions.

---

## 2. Reliability & data integrity

### Current state
- **No schema migration tool** — `spring.jpa.hibernate.ddl-auto=update` mutates the schema automatically, and the UUID migration was done via a hand-run SQL script (`psql_uuid_migration_steps.sql`) checked into `src/main/resources`. There's no repeatable, reviewable, rollback-able migration history.
- No documented backup/restore process for the Postgres data — nothing in either repo describes it, and this is personal habit-tracking data a user would be upset to lose.
- The offline-first sync layer (`dbSync.js`) has a real conflict-resolution policy ("server wins unless local is dirty or newer") but it's implemented ad hoc with comments, not tested, and 409 conflicts are silently skipped and "resolved next sync" — there's no test proving that resolution actually converges correctly.
- Error handling is inconsistent: some exceptions map to clean `@ResponseStatus` responses (`ResourceNotFoundException`, `ResourceConflictException`, `NotAuthorizedException`), but there's **no global `@ControllerAdvice`** — an unexpected exception (NPE, DB constraint violation) falls through to Spring Boot's default error response with no consistent shape and no correlation to server-side logs.
- Two confirmed data-correctness bugs already found and partly fixed this session: `ProjectDto` silently dropping `updatedAt` (fixed), and the `long[]`/`UUID[]` mismatches in `CommentRepository`'s category-filter queries (not yet fixed — likely still broken).
- `CommentController` bypasses the service layer entirely, meaning none of its logic benefits from whatever consistency patterns exist elsewhere.

### Target bar for this project
- Schema changes are reviewable and repeatable — not "run this SQL script by hand once."
- A documented, periodically-verified way to back up and restore the database exists (doesn't need to be automated to start, needs to exist and be *known to work*).
- Every unhandled exception returns a consistent, safe error shape — no stack traces leaking to clients, no silent 500s with nothing to search logs for.
- The offline-sync conflict model has at least one test proving convergence for the common case (concurrent edit on two devices).

### Gap → backlog
1. **P0** — Add a global `@RestControllerAdvice` mapping unhandled exceptions to a consistent JSON error shape with a correlation id.
2. **P0** — Introduce Flyway (lighter-weight than Liquibase for a project this size), baseline it against the current schema, and set `ddl-auto=validate` going forward.
3. **P0** — Fix the `CommentRepository` `long[]`/`UUID[]` type mismatches (already scoped in [Comments known gaps](features/05-comments.md#known-gaps)) — this is a live correctness bug, not a hardening item.
4. **P1** — Document (and periodically test) a Postgres backup/restore procedure — even a simple `pg_dump` cron + documented restore steps is a large improvement over nothing.
5. **P1** — Add a test for the Dexie sync conflict-resolution policy (concurrent edit scenario).
6. **P2** — Extract `CommentService` so comment logic is consistent with the rest of the backend (tracked in [Comments known gaps](features/05-comments.md#known-gaps)).

---

## 3. Observability

### Current state
- Logging is `log.debug`/`log.trace` scattered through services and controllers, at the developer's discretion — no structured logging, no request/correlation IDs, no consistent "what happened" trail for a given user action.
- `PerformanceAspect` (AOP around all controller methods) logs a warning when a call exceeds 500ms and a trace line above 200ms — a reasonable ad hoc slow-request signal, but it's the *only* metric in the system; there's no aggregation, no dashboard, nothing beyond a log line.
- `spring-boot-starter-actuator` is a dependency, but nothing is configured — default exposure is just `/actuator/health`, no metrics endpoint, no Micrometer registry (Prometheus, etc.) wired up.
- No alerting of any kind — a production outage or a spike in errors would only be noticed by a user complaining, or the maintainer checking logs manually.
- Frontend has no error tracking (no Sentry or equivalent, no React error boundary anywhere in the component tree) — an unhandled render error blanks the whole app with nothing captured.
- The frontend's service worker (`firebase-messaging-sw.js`) still loads Firebase **v8** via CDN `importScripts`, while the app itself is on Firebase **v12** (`package.json`) — a version mismatch that's easy to miss because nothing surfaces it.

### Target bar for this project
- A production error is discoverable within minutes, not "eventually, if a user mentions it."
- Logs are structured enough to trace one user's request end-to-end without grepping timestamps.
- At least one place (even just Actuator `/health` + `/metrics`, no dashboard required yet) exposes whether the app is actually healthy.
- The frontend doesn't go fully blank on an unhandled error — a fallback UI plus a captured report.

### Gap → backlog
1. **P1** — Add a minimal React `ErrorBoundary` at the app root so a render error shows a fallback instead of a blank screen.
2. **P1** — Expose Actuator `/health` and `/metrics` (Micrometer's built-in registry is enough to start — a Prometheus/Grafana pipeline is a later step, not a prerequisite).
3. **P2** — Add a request-correlation id (e.g. via a servlet filter) that's included in every log line for a given request — the highest-leverage single change for debuggability.
4. **P2** — Wire up basic error tracking on the frontend (Sentry's free tier or similar) — the app currently has zero visibility into client-side failures.
5. **P3** — Resolve the Firebase v8/v12 mismatch between the service worker and the main app.

---

## 4. Performance, testability & maintainability

### Current state
- **N+1 / lazy-loading risk:** `Project.toString()` and `Task.toString()` dereference lazy associations (`projectCategory.getName()`, `user.getEmail()`); since services log entities via `log.trace/debug("{}", entity)`, this triggers lazy loads (or `LazyInitializationException` outside a transaction) purely for logging. No `@EntityGraph` usage anywhere, so any future code that loads an entity by id and walks its associations will N+1.
- **Caching is mid-refactor and partly broken:** `ProjectCategoryService`'s cache annotations are commented out rather than removed; `AuthorityService.getAuthorities` caches by `User` with no key and no `equals`/`hashCode` override, so it never actually hits; `UserService.updatePassword` doesn't evict the `user` cache, risking a stale cached password being served.
- **No CI pipeline** in either repo — no GitHub Actions or equivalent, so nothing runs tests or builds on push/PR today. Every check in this session has been manual.
- **Frontend has no test runner wired up at all** — a leftover CRA `App.test.jsx` exists but there's no Vitest/Jest config in a Vite project; it's dead weight, not a safety net.
- **Backend test coverage is real but uneven** — good coverage now on `TaskService` (after this session's work), but `StatsService`, `TagService`, and the entire `security/` package have zero tests, and `AccountabilityPartnerService` (the one that gates cross-user data access) is untested too.
- Dependency and pattern debt already tracked in [ARCHITECTURE.md](ARCHITECTURE.md#known-gaps): `CommentController` has no service layer, raw-string status/type fields on `Project`/`Pomodoro`/`Comment` (Task's was fixed this session), leftover JSP/webjar dead code (`HelloWorld`, `bootstrap`/`jquery` webjars) from a pre-React UI.

### Target bar for this project
- Every push/PR at minimum compiles and runs the existing test suite automatically — the manual "did I break anything" loop this session has been running is not sustainable as the only check.
- The frontend has a working test runner, even if coverage starts near zero — the point is a floor to build on, not full coverage on day one.
- No entity association access inside a `toString()` or log statement that isn't already eagerly loaded.
- Caching is either correct or removed — "commented out, half-working" is worse than either extreme.

### Gap → backlog
1. **P0** — Set up CI (GitHub Actions) running `mvn test` on the backend and (once it exists) the frontend test suite on every push/PR. Highest leverage item in this whole document — every other fix here is safer once this exists.
2. **P0** — Wire up Vitest for the frontend, replace or delete the stale `App.test.jsx`.
3. **P1** — Fix the three caching bugs (`ProjectCategoryService` dead annotations, `AuthorityService` no-op cache, `UserService.updatePassword` missing evict) — small, contained, already fully scoped.
4. **P1** — Remove lazy-association access from `Task.toString()`/`Project.toString()`.
5. **P2** — Add tests for `AccountabilityPartnerService` (authorization-sensitive, zero coverage today) and `StatsService`.
6. **P3** — Clean up dead JSP/webjar code (`HelloWorld`, `bootstrap`/`jquery` webjars).

---

## Prioritized backlog

Everything above, merged into one sequence. P0 items are correctness/safety-net gaps worth doing before anything else in this list; P1s are the next-highest-leverage fixes; P2/P3 can wait.

| # | Priority | Item | Area |
|---|---|---|---|
| 1 | P0 | Set up CI (backend `mvn test`, frontend once it has a runner) on every push/PR | Testability |
| 2 | P0 | Wire up Vitest for the frontend; retire the stale `App.test.jsx` | Testability |
| 3 | P0 | Global `@RestControllerAdvice` for consistent error responses | Reliability |
| 4 | P0 | Introduce Flyway, baseline current schema, `ddl-auto=validate` | Reliability |
| 5 | P0 | Fix `CommentRepository` `long[]`/`UUID[]` mismatches (live bug) | Reliability |
| 6 | P0 | Accountability-partner invite/accept flow (closes consent gap + enumeration) | Security |
| 7 | P0 | Bump Spring Boot off EOL 3.0.x | Security |
| 8 | P1 | `@Valid` on every `@RequestBody` endpoint (start with `TaskController`, `CommentController`) | Security |
| 9 | P1 | Rate limit `/accountability-partners` POST and login/signup | Security |
| 10 | P1 | Document + verify a Postgres backup/restore procedure | Reliability |
| 11 | P1 | Test the Dexie sync conflict-resolution policy | Reliability |
| 12 | P1 | React `ErrorBoundary` at the app root | Observability |
| 13 | P1 | Expose Actuator `/health` + `/metrics` | Observability |
| 14 | P1 | Fix the three caching bugs | Maintainability |
| 15 | P1 | Remove lazy-association access from `toString()` methods | Performance |
| 16 | P2 | Request-correlation id in logs | Observability |
| 17 | P2 | Frontend error tracking (Sentry or similar) | Observability |
| 18 | P2 | Fix frontend JWT-refresh-failure handling | Security |
| 19 | P2 | Bump `firebase-admin`, `springdoc-openapi` | Security |
| 20 | P2 | Tests for `AccountabilityPartnerService`, `StatsService` | Testability |
| 21 | P2 | Extract `CommentService` | Maintainability |
| 22 | P3 | Fix Firebase v8/v12 mismatch (SW vs. main app) | Observability |
| 23 | P3 | Clean up dead JSP/webjar code | Maintainability |
