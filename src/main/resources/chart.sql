select count(*), task_id 
from pomodoros 
where user_id=202 and status='completed' group by task_id;


select count(*), t.description as task
from pomodoros as p
join tasks as t on p.task_id = t.id
where p.user_id=202 and p.status='completed' group by t.description;

select count(*), pp.name as project
from pomodoros as p
join tasks as t on p.task_id = t.id
join projects as pp on t.project_id = pp.id
where p.user_id=202 and p.status='completed' group by pp.name;

select sum(p.time_elapsed) / 60 as time, pp.name as project
from pomodoros as p
join tasks as t on p.task_id = t.id
join projects as pp on t.project_id = pp.id
where p.user_id=1 and p.status='completed' 
group by pp.name;

-- JPQL
select sum(p.timeElapsed) / 60 as time, p.task.project.name as project
from pomodoros as p
where p.user.id=?1 and p.status='completed' 
group by p.task.project.name

-- get color also
select sum(p.time_elapsed) / 60 as time, pp.name, pp.color as project
from pomodoros as p
join tasks as t on p.task_id = t.id
join projects as pp on t.project_id = pp.id
where p.user_id=1 and p.status='completed' 
group by pp.name, pp.color;

-- JPQL
select sum(p.timeElapsed) / 60 as time, p.task.project.name as project, p.task.project.color as color
from pomodoros as p
where p.user.id=?1 and p.status='completed' 
group by p.task.project.name, p.task.project.color

select p.end_time::date, sum(p.time_elapsed) / 60 as time, pp.name as project
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			where p.user_id=1 and p.status='completed'
			group by pp.name, p.end_time::date
			order by pp.name;