# Accountability Partners

## What it does

Lets one user ("subject") share stats with another ("partner"), scoped by category (`ProjectCategory.visibleToPartners`) — the README's "Make It Satisfying" section describes this as allowing "Accountability Partners to collaborate using shared stats of selected project categories."

## Backend

**Entity:** `AccountabilityPartner` (`habits-pomodoro/src/main/java/com/anshul/atomichabits/model/AccountabilityPartner.java`)

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | auto-generated — one of two entities still on `Long` ids post-UUID-migration (the other is `UserSettings`) |
| `subject` | `User` | the user whose data is being shared |
| `partner` | `User` | the user granted viewing access |

Unique constraint on `(subject_id, partner_id)`.

**Service:** `AccountabilityPartnerService` (`business/AccountabilityPartnerService.java`) — `isSubject(userId, subjectId)` is the authorization gate used throughout `PomodoroController`'s stats endpoints (see [Stats](06-stats.md)) to check whether the current user is allowed to view `subjectId`'s data.

**Endpoints:** `AccountabilityPartnerController` (`controller/AccountabilityPartnerController.java`)

| Method | Path | Purpose |
|---|---|---|
| GET | `/accountability-partners` | list users I've added as partners (I am the subject) |
| GET | `/accountability-subjects` | list users who added me as their partner |
| POST | `/accountability-partners` | add a partner by email |
| DELETE | `/accountability-partners/{id}` | remove |

`POST /accountability-partners` returns `409 CONFLICT` if the pair already exists, `404 NOT_FOUND` if the email doesn't match any account.

## Frontend

**Components:**
- `src/components/user-settings/ListAccountabilityPartnersComponent.jsx` — add/remove partners, under Settings
- `src/components/stats/SelectFriendsComponent.jsx` — pick which partner's data to view in Stats
- `src/components/stats/CategoryChecklistComponent.jsx` — pick which of that partner's visible categories to include

**Data flow:** Not part of the Dexie `apiMap` sync layer — partner list and stats-viewing are live API calls via `AccountabilityPartnerApiService.js`, not offline-cached.

## Known gaps

- **Security/UX gap:** creating a partnership is unilateral — any authenticated user can add any other existing account as a partner by email, with no invite/accept step from the target user. Combined with the distinguishable `409` (already linked) vs `404` (no such email) responses, this is also a user-enumeration vector: an attacker can probe arbitrary email addresses to learn which have accounts.
- Zero backend tests for `AccountabilityPartnerService`, despite `isSubject` being the authorization check that gates cross-user data access throughout stats.
- `AccountabilityPartner` still uses a `Long` auto-generated id rather than `UUID`, inconsistent with most other entities post-migration (harmless today since nothing exposes this id externally in a way that assumes UUID, but worth knowing if extending the model).
