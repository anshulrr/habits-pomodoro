# Projects & Project Categories

## What it does

Two levels of grouping above Task, matching the README's "Make It Obvious" guidance: put related habits into **Projects**, and group projects into **Project Categories** for higher-level visualization and access. Categories also gate what's visible to accountability partners (`visibleToPartners`).

## Backend

**Entities:** `Project` and `ProjectCategory` (`habits-pomodoro/src/main/java/com/anshul/atomichabits/model/`)

**`ProjectCategory` fields:**

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | |
| `name` | `String` | |
| `level` | `Integer` | display ordering |
| `statsDefault` | `boolean` | default true — included in stats views by default |
| `visibleToPartners` | `boolean` | default true — gates accountability-partner visibility |
| `color` | `String` | hex, default `#818181` |
| `projects` | `List<Project>` | `@OneToMany`, mapped by `projectCategory` |

**`Project` fields:**

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | |
| `name`, `description` | `String` | |
| `color` | `String` | hex, default `#228B22` |
| `pomodoroLength` | `Integer` | default pomodoro length for tasks in this project |
| `status` | `String` | `current` / `archived` |
| `priority` | `Integer` | ordering |
| `type` | `String` | `neutral` / `good` / `bad` — mirrors `Task.type` |
| `dailyLimit` | `Integer` | project-level pomodoro cap |
| `projectCategory` | `ProjectCategory` | `@ManyToOne`, lazy |
| `tasks` | `List<Task>` | `@OneToMany`, mapped by `project` |

**Services:** `ProjectService`, `ProjectCategoryService` (`business/`)

**Endpoints:**

`ProjectController`:

| Method | Path |
|---|---|
| GET | `/projects/{id}` |
| GET | `/projects` |
| GET | `/projects/count` |
| POST | `/projects` |
| PUT | `/projects/{id}` |

`ProjectCategoryController`:

| Method | Path |
|---|---|
| GET | `/project-categories/{id}` |
| GET | `/project-categories` |
| GET | `/project-categories/count` |
| POST | `/project-categories` |
| PUT | `/project-categories/{id}` |

## Frontend

**Components:**
- `src/components/features/projects/ListProjectsComponents.jsx`, `ProjectComponent.jsx` — project list/detail, used from the home view and task-list filters
- `src/components/user-settings/ListProjectsCategoriesComponent.jsx`, `ProjectCategoryComponent.jsx` — category management lives under Settings, not under a dedicated "categories" feature folder

**Data flow:** Both `categories` and `projects` are routed through the Dexie sync layer (`services/db/dbConfig.js`), backed by `ProjectCategoryApiService.js` / `ProjectApiService.js`. Note: `projects` uses the same function for `retrieveAllApi` and `retrieveSyncAllApi` (no separate delta endpoint), unlike `categories`/`tasks`/`comments`, which do have a distinct `/sync` style pull — worth confirming this is intentional if project sync ever needs to scale.

## Known gaps

- `Project.type`/`status` are raw strings, same enum gap as `Task` (see [Tasks](01-tasks.md)).
- `ProjectCategoryService`'s Redis cache annotations are commented out rather than removed or fixed (see backend [ARCHITECTURE.md](../ARCHITECTURE.md#caching)) — category reads currently bypass caching entirely, mid-cleanup.
- Category management UI lives under Settings rather than alongside Projects/Tasks — not a bug, but worth knowing when looking for it.
