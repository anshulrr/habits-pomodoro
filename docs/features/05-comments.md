# Comments

## What it does

Freeform notes attachable to a Category, Project, Task, or Pomodoro — used throughout the README's feature list for things like recording *why* a bad-habit violation happened, noting a location/context cue, or logging reframe reasons ("repulsion bundling"). One comment attaches to exactly one parent record.

## Backend

**Entity:** `Comment` (`habits-pomodoro/src/main/java/com/anshul/atomichabits/model/Comment.java`)

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | |
| `description` | `String` | the note text |
| `status` | `String` | `added` (default), presumably more values exist elsewhere |
| `reviseDate` | `Instant` | for a later-review workflow |
| `projectCategory`, `project`, `task`, `pomodoro` | each `@ManyToOne`, lazy | **all nullable — exactly one is set per comment**, no polymorphic/discriminator column |
| `tags` | `Set<Tag>` | `@ManyToMany` via `comments_tags` |

**Architectural note:** `CommentController` has no corresponding `CommentService` — it's the one feature where controller code talks to repositories directly (`UserRepository`, `ProjectCategoryRepository`, `ProjectRepository`, `TaskRepository`, `PomodoroRepository`, `CommentRepository`, `TagRepository`, all injected into the controller). See backend [ARCHITECTURE.md](../ARCHITECTURE.md#layering).

**Endpoints:** `CommentController` (`controller/CommentController.java`) — the largest controller in the app, with both generic and parent-scoped routes:

| Method | Path | Purpose |
|---|---|---|
| GET | `/comments` | list, filtered by `categoryIds` etc. |
| GET | `/comments/sync` | delta pull for the offline sync layer |
| GET | `/comments/count` | |
| POST | `/comments` | create (parent inferred from payload) |
| GET / PUT | `/comments/{id}` | fetch / update one |
| POST | `/comments/{id}/tags` | attach tags |
| GET | `/comments/tags` | |
| GET / GET count / POST | `/project-categories/{categoryId}/comments...` | category-scoped |
| GET / GET count / POST | `/projects/{projectId}/comments...` | project-scoped |
| GET / GET count / POST | `/tasks/{taskId}/comments...` | task-scoped |
| GET / GET count / POST | `/pomodoros/{pomodoroId}/comments...` | pomodoro-scoped |

## Frontend

**Components:** `src/components/features/comments/`
- `ListCommentsComponent.jsx`, `ListFilteredCommentsComponents.jsx` — list views, generic and filtered
- `CommentComponent.jsx`, `UpdateCommentComponent.jsx` — single-comment display/edit
- `CommentsFilterComponent.jsx`, `SearchCommentComponent.jsx` — filtering/search
- `InsertLinkComponent.jsx` — rich-text link insertion into a comment body
- `UserCommentsComponent.jsx` — the "all my comments" aggregate view

**Data flow:** `comments` is one of the four entities in the Dexie sync map, backed by `CommentApiService.js`, with its own delta-pull endpoint (`GET /comments/sync`) mirrored by `retrieveSyncAllCommentsApi`.

## Known gaps

- **Likely broken category filtering:** `CommentRepository`'s list queries (`retrieveUserComments`, `retrieveUserCommentsWithReviseDate`, `retrieveUserSearchedComments`) take `long[] categoryIds`, while their sibling *count* queries correctly take `UUID[] categoryIds` — since `project_category_id` is a `uuid` column post-migration, filtering `GET /comments` by category is likely broken today. `CommentController.retrieveComments` also declares the param as `long[]`, confirming the bug traces back to the controller layer.
- No `CommentService` — all logic is inline in the controller, making this the least testable feature in the backend and the natural first extraction before adding comment features.
- Zero backend tests found for comments.
