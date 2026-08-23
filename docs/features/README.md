# Feature Documentation — Habits Pomodoro

Each doc below covers one feature end-to-end: the backend model + API, the frontend components + local-sync behavior, and known gaps specific to that feature. For layer-by-layer architecture (not feature-by-feature), see:

- [`habits-pomodoro/docs/ARCHITECTURE.md`](../ARCHITECTURE.md) — backend
- [`habits-pomodoro-frontend/docs/ARCHITECTURE.md`](../../../habits-pomodoro-frontend/docs/ARCHITECTURE.md) — frontend

All docs reflect the code as of 2026-08-14, not an idealized design — flagged gaps are real, found by reading the source.

## Features

| Doc | Covers |
|---|---|
| [01-tasks.md](01-tasks.md) | The core habit unit: description, due dates, streak-relevant fields, tagging |
| [02-projects-and-categories.md](02-projects-and-categories.md) | Grouping tasks into projects, projects into categories |
| [03-pomodoro-timer.md](03-pomodoro-timer.md) | The timer itself: start/pause/complete state machine, past-pomodoro entry, breaks |
| [04-tags.md](04-tags.md) | Cross-cutting labels on tasks and comments |
| [05-comments.md](05-comments.md) | Notes attachable to categories, projects, tasks, or pomodoros |
| [06-stats.md](06-stats.md) | Charts and aggregate stats across tasks/projects/categories |
| [07-accountability-partners.md](07-accountability-partners.md) | Sharing stats with another user |
| [08-auth-and-settings.md](08-auth-and-settings.md) | Firebase-backed login/signup, per-user settings, notifications |

## Reading a doc

Each doc follows the same shape:

1. **What it does** — tied back to the product concept in the [root README](../../README.md)
2. **Backend** — entity fields, API endpoints, notable service logic
3. **Frontend** — components, services, and how it flows through the offline-first Dexie sync layer
4. **Known gaps** — bugs or inconsistencies specific to this feature, found during the 2026-08 code audit
