# Tasks

## What it does

A Task is the unit of habit being tracked — "the task of the habit you want to monitor," per the [root README](../../README.md). A task belongs to a Project, can be typed as `good` / `bad` / `neutral`, carries streak-relevant scheduling (due date, repeat interval, daily pomodoro limit), and accumulates Pomodoro runs.

## Backend

**Entity:** `Task` (`habits-pomodoro/src/main/java/com/anshul/atomichabits/model/Task.java`)

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | |
| `description` | `String` | required |
| `estimatedPomodorosCount` | `Integer` | default 0 |
| `pomodoroLength` | `Integer` | minutes, default 0 (falls back to user setting) |
| `dueDate` | `Instant` | drives cue/reminder behavior |
| `status` | `TaskStatus` | `CURRENT` / `ARCHIVED` — enum, `@Convert`ed to the same `current`/`archived` DB and wire strings as before |
| `type` | `TaskType` | `NEUTRAL` / `GOOD` / `BAD` — enum, `@Convert`ed to the same `neutral`/`good`/`bad` DB and wire strings as before |
| `priority` | `Integer` | used for drag-reorder ordering |
| `repeatDays` | `Integer` | days between repeats, for habit-stacking / recurring due dates |
| `dailyLimit` | `Integer` | pomodoros/day cap — relevant for `bad` habits ("increase friction") |
| `enableNotifications` | `boolean` | gates FCM push for this task |
| `project` | `Project` | `@ManyToOne`, lazy |
| `tags` | `Set<Tag>` | `@ManyToMany` via `tasks_tags` join table |
| `pomodoros` | `List<Pomodoro>` | `@OneToMany`, mapped by `task` |

**Service:** `TaskService` (`business/TaskService.java`)

**Endpoints:** `TaskController` (`controller/TaskController.java`)

| Method | Path | Purpose |
|---|---|---|
| GET | `/tasks/{taskId}` | single task |
| GET | `/tasks` | list, filterable |
| GET | `/tasks/count` | count for pagination |
| POST | `/tasks` | create |
| PUT | `/tasks/{id}` | update |
| PUT | `/tasks/{id}/priority` | reorder (drag-and-drop) |
| PUT | `/projects/{id}/priority-reset` | renormalize priority integers within a project |
| POST | `/tasks/{id}/tags` | attach/replace tags on one task |
| POST | `/tasks/tags` | bulk tag sync across tasks |
| GET | `/tasks/pomodoros/time-elapsed` | today's elapsed pomodoro time per task, for progress display |
| POST | `/tasks/comments/count` | comment counts for a batch of tasks |

**Priority/reordering:** `priority` is a plain integer gap-based ordering column (frontend computes the gap — see below); `priority-reset` exists to renormalize when gaps run out.

## Frontend

**Components:** `src/components/features/tasks/`
- `ListTasksComponent.jsx` / `ListTasksRowsComponent.jsx` — list + pagination
- `SortableTask.jsx` — single row: drag-reorder, inline actions, popup menus (largest component in this feature, ~420 lines)
- `CreateTaskComponent.jsx` / `UpdateTaskComponent.jsx` — forms
- `SwitchProjectComponent.jsx` — move a task between projects
- `DueDateInputComponent.jsx` / `TaskDueDateComponent.jsx` — due-date + repeat UI
- `SearchTaskComponent.jsx` — filter/search
- `PastPomodoroComponent.jsx` — log a pomodoro that already happened (see [Pomodoro Timer](03-pomodoro-timer.md))
- `TaskStats.jsx` — per-task stats panel (see [Stats](06-stats.md))

**Data flow:** Tasks are one of the four entities routed through the offline-first sync layer (`services/db/dbConfig.js` → `apiMap.tasks`, backed by `TaskApiService.js`). Reads/writes go to Dexie first; `dbSync.js` reconciles with `TaskController` in the background. See [frontend ARCHITECTURE.md](../../../habits-pomodoro-frontend/docs/ARCHITECTURE.md#local-first-data-flow-dexie) for the sync mechanism.

**Priority/reorder client-side:** `SortableTask.jsx` computes an integer gap between neighboring tasks' `priority` values on drag, rather than re-sequencing every row — cheap locally, but relies on the backend `priority-reset` endpoint once gaps are exhausted.

## Known gaps

- ~~`Task.status` and `Task.type` are raw strings with no enum or DB constraint.~~ **Fixed 2026-08-23:** both are now `TaskStatus`/`TaskType` Java enums (`model/TaskStatus.java`, `model/TaskType.java`), applied to the entity via a JPA `AttributeConverter` (`model/converter/`) so the DB column and JSON wire format are byte-for-byte unchanged — verified against a real Postgres instance, including that `GET /tasks` list JSON still serializes as `"current"`/`"neutral"` etc., not `"CURRENT"`/`"NEUTRAL"`. A typo'd status/type in a `PUT /tasks/{id}` body (e.g. `"complete"` instead of `"completed"`... `"current"`/`"archived"` are the only valid values) now fails deserialization with a 400 instead of silently persisting and never matching any filter query again. Query-string filters (`GET /tasks?status=...`) go through new `Converter<String, TaskStatus>` beans (`web/StringToTaskStatusConverter.java`, `web/StringToTaskTypeConverter.java`) so `?status=current` still works. Repository methods split along JPQL vs. native-SQL lines: JPQL methods (`retrieveUserTasksByProjectId`, `retrieveFilteredTasks`, `findTasksByUserIdAndTagsId`, `getTagsTasksCount`, `getNotificationTasks`) now take the enum type directly — Hibernate 6 enforces exact parameter-type matching against a converted attribute path and throws `InvalidDataAccessApiUsageException` otherwise; native-SQL methods (`getProjectTasksCount`, `getFilteredTasksCount`, `retrieveSearchedTasks`, `getSearchedTasksCount`) keep `String` params since native queries bind raw JDBC values outside the converter. `Project.status`/`type`, `Pomodoro.status`, and `Comment.status` are still raw strings — same gap, out of scope for this pass.
- ~~No tests found for the priority-gap / reorder logic on either side.~~ **Fixed 2026-08-23:** `TaskServiceTest` now covers `createTask`'s initial-priority assignment and all three branches of `updateTaskPriority` (drop at start, drop at end, drop between two tasks), plus the not-found and reset-priority paths. Writing these tests surfaced two things worth tracking separately:
  - **Adjacent-priority collision:** `updateTaskPriority`'s midpoint formula (`(prevOrder + nextOrder) / 2`) integer-divides, so dropping a task between two priorities that differ by 1 (e.g. 1000 and 1001) produces a priority equal to `prevOrder` (1000) — a collision, not a distinct position. The gap-based scheme relies on `priority-reset` being called before this happens in practice; there's no guard against it happening anyway. Frontend-only mitigation today, nothing enforced server-side.
  - **Pre-existing failing test, unrelated to priority logic:** `TaskServiceTest.updateTask` throws an NPE (`taskEntry.get().getUpdatedAt()` is null when compared against `taskDto.updatedAt()`) because the `Task` object built via the test-only constructor never sets `updatedAt`. This test was already broken before this pass — it's a test-data gap, not a `TaskService` bug — and is failing on `main`/`refactor/audit` independent of any change made here.
- `dailyLimit` and `repeatDays` exist on the model but there's no dedicated `StreakService` — streak/limit logic appears to be computed ad hoc from raw pomodoro data rather than being a first-class backend concept (see the note in [Stats](06-stats.md)). *(not yet fixed)*
