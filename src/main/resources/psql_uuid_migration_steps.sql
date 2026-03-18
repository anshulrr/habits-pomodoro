-- foreign keys
ALTER TABLE comments DROP CONSTRAINT fkgkoamotsfr3mc0pwa1qrrmwhi;
ALTER TABLE comments DROP CONSTRAINT fk39x1yvfcb7u9vw4vxfw4xjylb;
ALTER TABLE comments DROP CONSTRAINT fkoi1ijhdoq18siqdy22emlt4cy;
ALTER TABLE comments DROP CONSTRAINT fki7pp0331nbiwd2844kg78kfwb;
ALTER TABLE tasks DROP CONSTRAINT fksfhn82y57i3k9uxww1s007acc;
ALTER TABLE projects DROP CONSTRAINT fkrdsdbabjguq8yf5sdyhd8guvp;
ALTER TABLE pomodoros DROP CONSTRAINT fkaac72g1ut1d0f6p3kdukh6llq;
ALTER TABLE tasks_tags DROP CONSTRAINT fk8sfpkkqd4x6n112sxtl4k7yb9;
ALTER TABLE tasks_tags DROP CONSTRAINT fk92yt3qd8g5o4jm1ck3ca69vbp;
ALTER TABLE comments_tags DROP CONSTRAINT fk35q1m4qgraagu7lip8nbguym6;
ALTER TABLE comments_tags DROP CONSTRAINT fkb60um1dsxeo8gx4ndkyyg35as;

-- pomodoros
ALTER TABLE pomodoros RENAME COLUMN id TO old_id;
ALTER TABLE pomodoros RENAME COLUMN public_id TO id;
-- comments
ALTER TABLE comments RENAME COLUMN id TO old_id;
ALTER TABLE comments RENAME COLUMN public_id TO id;
-- tags
ALTER TABLE tags RENAME COLUMN id TO old_id;
ALTER TABLE tags RENAME COLUMN public_id TO id;
-- comments_tags
ALTER TABLE comments_tags RENAME COLUMN comment_id TO comment_old_id;
ALTER TABLE comments_tags ADD COLUMN comment_id uuid;
ALTER TABLE comments_tags RENAME COLUMN tag_id TO tag_old_id;
ALTER TABLE comments_tags ADD COLUMN tag_id uuid;
-- tasks
ALTER TABLE tasks RENAME COLUMN id TO old_id;
ALTER TABLE tasks RENAME COLUMN public_id TO id;
-- projects
ALTER TABLE projects RENAME COLUMN id TO old_id;
ALTER TABLE projects RENAME COLUMN public_id TO id;
-- project_categories
ALTER TABLE project_categories RENAME COLUMN id TO old_id;
ALTER TABLE project_categories RENAME COLUMN public_id TO id;
-- tasks_tags
ALTER TABLE tasks_tags RENAME COLUMN task_id TO task_old_id;
ALTER TABLE tasks_tags ADD COLUMN task_id uuid;
ALTER TABLE tasks_tags RENAME COLUMN tag_id TO tag_old_id;
ALTER TABLE tasks_tags ADD COLUMN tag_id uuid;

-- cleanup
update tasks set id = gen_random_uuid() where id is null; 
update pomodoros set id = gen_random_uuid() where id is null;
update comments set id = gen_random_uuid() where id is null;
update tags set id = gen_random_uuid() where id is null;
update projects set id = gen_random_uuid() where id is null;
update project_categories set id = gen_random_uuid() where id is null;









-- pomodoros
ALTER TABLE pomodoros DROP CONSTRAINT pomodoros_pkey;
ALTER TABLE pomodoros ADD PRIMARY KEY (id);
ALTER TABLE pomodoros ALTER COLUMN old_id DROP NOT NULL;

ALTER TABLE pomodoros RENAME COLUMN task_id TO task_old_id;
ALTER TABLE pomodoros ADD COLUMN task_id uuid;
UPDATE pomodoros SET task_id = tasks.id FROM tasks WHERE pomodoros.task_old_id = tasks.old_id;


-- tags
ALTER TABLE tags DROP CONSTRAINT tags_pkey;
ALTER TABLE tags ADD PRIMARY KEY (id);
ALTER TABLE tags ALTER COLUMN old_id DROP NOT NULL;

-- comments_tags
UPDATE comments_tags SET comment_id = comments.id FROM comments WHERE comments_tags.comment_old_id = comments.old_id;

UPDATE comments_tags SET tag_id = tags.id FROM tags WHERE comments_tags.tag_old_id = tags.old_id;

ALTER TABLE comments_tags DROP CONSTRAINT comments_tags_pkey;
ALTER TABLE comments_tags ADD PRIMARY KEY (comment_id, tag_id);
ALTER TABLE comments_tags ALTER COLUMN comment_old_id DROP NOT NULL;
ALTER TABLE comments_tags ALTER COLUMN tag_old_id DROP NOT NULL;

-- tasks_tags
UPDATE tasks_tags SET task_id = tasks.id FROM tasks WHERE tasks_tags.task_old_id = tasks.old_id;

UPDATE tasks_tags SET tag_id = tags.id FROM tags WHERE tasks_tags.tag_old_id = tags.old_id;

ALTER TABLE tasks_tags DROP CONSTRAINT tasks_tags_pkey;
ALTER TABLE tasks_tags ADD PRIMARY KEY (task_id, tag_id);
ALTER TABLE tasks_tags ALTER COLUMN task_old_id DROP NOT NULL;
ALTER TABLE tasks_tags ALTER COLUMN tag_old_id DROP NOT NULL;







-- projects
ALTER TABLE tasks RENAME COLUMN project_id TO project_old_id;
ALTER TABLE tasks ADD COLUMN project_id uuid;
UPDATE tasks SET project_id = projects.id FROM projects WHERE tasks.project_old_id = projects.old_id;

-- tasks
ALTER TABLE tasks DROP CONSTRAINT tasks_pkey;
ALTER TABLE tasks ADD PRIMARY KEY (id);
ALTER TABLE tasks ALTER COLUMN old_id DROP NOT NULL;

-- projects
ALTER TABLE projects DROP CONSTRAINT projects_pkey;
ALTER TABLE projects ADD PRIMARY KEY (id);
ALTER TABLE projects ALTER COLUMN old_id DROP NOT NULL;

-- project_categories
ALTER TABLE projects RENAME COLUMN project_category_id TO project_category_old_id;
ALTER TABLE projects ADD COLUMN project_category_id uuid;
UPDATE projects SET project_category_id = project_categories.id FROM project_categories WHERE projects.project_category_old_id = project_categories.old_id;

ALTER TABLE project_categories DROP CONSTRAINT project_categories_pkey;
ALTER TABLE project_categories ADD PRIMARY KEY (id);
ALTER TABLE project_categories ALTER COLUMN old_id DROP NOT NULL;

-- comments
ALTER TABLE comments DROP CONSTRAINT comments_pkey;
ALTER TABLE comments ADD PRIMARY KEY (id);
ALTER TABLE comments ALTER COLUMN old_id DROP NOT NULL;

ALTER TABLE comments RENAME COLUMN pomodoro_id TO pomodoro_old_id;
ALTER TABLE comments ADD COLUMN pomodoro_id uuid;
UPDATE comments SET pomodoro_id = pomodoros.id FROM pomodoros WHERE comments.pomodoro_old_id = pomodoros.old_id;

ALTER TABLE comments RENAME COLUMN task_id TO task_old_id;
ALTER TABLE comments ADD COLUMN task_id uuid;
UPDATE comments SET task_id = tasks.id FROM tasks WHERE comments.task_old_id = tasks.old_id;

ALTER TABLE comments RENAME COLUMN project_id TO project_old_id;
ALTER TABLE comments ADD COLUMN project_id uuid;
UPDATE comments SET project_id = projects.id FROM projects WHERE comments.project_old_id = projects.old_id;

ALTER TABLE comments RENAME COLUMN project_category_id TO project_category_old_id;
ALTER TABLE comments ADD COLUMN project_category_id uuid;
UPDATE comments SET project_category_id = project_categories.id FROM project_categories WHERE comments.project_category_old_id = project_categories.old_id;








-- foreign keys
ALTER TABLE comments_tags 
ADD CONSTRAINT fk_tag 
FOREIGN KEY (tag_id) REFERENCES tags(id);
ALTER TABLE comments_tags 
ADD CONSTRAINT fk_comment 
FOREIGN KEY (comment_id) REFERENCES comments(id);

ALTER TABLE tasks_tags 
ADD CONSTRAINT fk_task 
FOREIGN KEY (task_id) REFERENCES tasks(id);
ALTER TABLE tasks_tags 
ADD CONSTRAINT fk_tag 
FOREIGN KEY (tag_id) REFERENCES tags(id);

-- tasks
ALTER TABLE tasks
ADD CONSTRAINT fk_project
FOREIGN KEY (project_id) REFERENCES projects(id);

-- pomodoros
ALTER TABLE pomodoros 
ADD CONSTRAINT fk_task 
FOREIGN KEY (task_id) REFERENCES tasks(id);

-- projects
ALTER TABLE projects
ADD CONSTRAINT fk_project_category
FOREIGN KEY (project_category_id) REFERENCES project_categories(id);

-- comments
ALTER TABLE comments 
ADD CONSTRAINT fk_pomodoro 
FOREIGN KEY (pomodoro_id) REFERENCES pomodoros(id);
ALTER TABLE comments 
ADD CONSTRAINT fk_task 
FOREIGN KEY (task_id) REFERENCES tasks(id);
ALTER TABLE comments 
ADD CONSTRAINT fk_project 
FOREIGN KEY (project_id) REFERENCES projects(id);
ALTER TABLE comments 
ADD CONSTRAINT fk_project_category 
FOREIGN KEY (project_category_id) REFERENCES project_categories(id);
