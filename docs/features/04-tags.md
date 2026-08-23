# Tags

## What it does

Cross-cutting labels, independent of the Category → Project → Task hierarchy — the README calls out grouping habits "by Tags (daily, imp) for easy access," e.g. a `daily` tag to surface habit-stacked tasks in one place regardless of which project they belong to.

## Backend

**Entity:** `Tag` (`habits-pomodoro/src/main/java/com/anshul/atomichabits/model/Tag.java`)

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | |
| `name` | `String` | |
| `priority` | `Integer` | ordering |
| `color` | `String` | hex, default `#818181` |
| `tasks` | `Set<Task>` | `@ManyToMany`, inverse side of `tasks_tags` |

Tags also attach to `Comment` via a separate `comments_tags` join table (see [Comments](05-comments.md)) — the same `Tag` entity is reused across both relations.

**Service:** `TagService` (`business/TagService.java`)

**Endpoints:** `TagController` (`controller/TagController.java`)

| Method | Path |
|---|---|
| GET | `/tags/{id}` |
| GET | `/tags` |
| GET | `/tags/count` |
| POST | `/tags` |
| PUT | `/tags/{id}` |

Task-tag and comment-tag attachment happens through `TaskController` (`POST /tasks/{id}/tags`, `POST /tasks/tags`) and `CommentController` (`POST /comments/{id}/tags`, `GET /comments/tags`) respectively, not through `TagController` itself.

## Frontend

**Components:** `src/components/features/tags/`
- `ListTagsComponents.jsx`, `CreateTagComponent.jsx`, `UpdateTagComponent.jsx` — tag CRUD
- `MapTagComponent.jsx` — attach/detach tags on a task
- `MapCommentTagsComponent.jsx` — attach/detach tags on a comment

**Data flow:** `tags` is one of the four entities in the Dexie sync map (`services/db/dbConfig.js`), backed by `TagApiService.js`, and is exposed as a live `tagsMap` via `DataContext.jsx`.

## Known gaps

- No dedicated tests for `TagService`.
- Two separate join tables (`tasks_tags`, `comments_tags`) exist for the same `Tag` entity — reasonable, but means any future "delete tag" flow has to clean up both relations, and it's worth checking `TagService`'s delete/update path actually does so.
