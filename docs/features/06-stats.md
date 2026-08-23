# Stats

## What it does

Aggregate and per-entity charts of pomodoro time across tasks, projects, and categories, in different time frames — the README's "Make It Satisfying" section ties this directly to habit-tracker immediate-reward feedback. Also the surface where an accountability partner's shared data gets viewed.

## Backend

**Service:** `StatsService` (`business/StatsService.java`) — a thin orchestration layer over native/JPQL aggregate queries on `PomodoroRepository`. No dedicated entity of its own; it reads from `Pomodoro` joined up through `Task → Project → ProjectCategory`.

Key methods:
- `retrieveProjectCategoriesPomodoros` / `retrieveProjectPomodoros` / `retrieveTaskPomodoros` — time-bucketed pomodoro time per category/project/task, scoped by date range and category filter
- `retrieveProjectsTotalPomodoros` / `retrieveTasksTotalPomodoros` — totals bucketed by a `limit` string (`daily`/`weekly`/`monthly`/etc., mapped internally to a SQL date-truncation format)
- `retrievePomodorosCount` — raw pomodoro counts over a range
- `retrieveTaskPomodoros(userId, taskId, limit, offset)` / `retrieveTaskPomodorosCount(userId, taskId)` — single-task pomodoro history, paginated

**Endpoints:** all on `PomodoroController` (`controller/PomodoroController.java`) rather than a separate `StatsController`:

| Method | Path | Purpose |
|---|---|---|
| GET | `/stats/project-categories-time` | time series by category |
| GET | `/stats/projects-time` | time series by project |
| GET | `/stats/tasks-time` | time series by task |
| GET | `/stats/projects-total-time` | totals by project, bucketed |
| GET | `/stats/tasks-total-time` | totals by task, bucketed |
| GET | `/stats/pomodoros-count` | raw count over range |
| GET | `/stats/task-pomodoros` | one task's pomodoro history, paginated |
| GET | `/stats/task-pomodoros/count` | count for that pagination |

Most stats endpoints accept a `subjectId` (or similar) parameter so an authenticated user can view a partner's data — gated by `AccountabilityPartnerService.isSubject` (see [Accountability Partners](07-accountability-partners.md)). That authorization check is duplicated across ~6 methods in `PomodoroController` rather than centralized.

## Frontend

**Components:** `src/components/stats/`
- `StatsComponent.jsx` — top-level stats view, tabs across tasks/projects/categories
- `TaskStats.jsx` (lives in `features/tasks/` but is part of this feature) — per-task drill-down
- `charts/` — `BarChart.jsx`, `DoughnutChart.jsx`, and per-domain wrappers (`TasksChart.jsx`, `ProjectsDistributionChart.jsx`, `ProjectCategoriesChart.jsx`, `TotalChart.jsx`, `StreakChart.jsx`), all built on Chart.js via `react-chartjs-2`
- `StatsSettingsComponent.jsx` — chart-type/time-frame preferences, persisted to `UserSettings`
- `SelectFriendsComponent.jsx` / `CategoryChecklistComponent.jsx` — pick which accountability partner and which categories to view
- `ListPomodorosComponent.jsx` — raw pomodoro list, not chart-based

**Data flow:** Stats reads go straight to the API (`StatsService` results aren't small enough to sync into IndexedDB as a general rule) — unlike Tasks/Projects/Categories/Tags/Comments, stats are not part of the Dexie `apiMap` sync layer.

## Known gaps

- **Likely broken endpoint:** `PomodoroRepository.getTaskPomodorosCount(Long userId, Long taskId)` (backing `retrieveTaskPomodorosCount` and `GET /stats/task-pomodoros/count`) still takes `Long taskId`, while the sibling `findTaskPomodorosCount` a few lines below correctly takes `UUID taskId` — `task_id` is a `uuid` column, so this endpoint is very likely broken since the UUID migration.
- Zero backend tests for `StatsService`, despite it being the most query-heavy, easiest-to-silently-break part of the backend.
- Streaks appear to be computed client-side from raw pomodoro/task data rather than being served by a dedicated backend concept — see the note in [Tasks](01-tasks.md).
- The `subjectId`/partner-authorization check is duplicated ~6 times across `PomodoroController` rather than factored into one place.
