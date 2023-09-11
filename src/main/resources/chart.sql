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
			
select date_trunc('day', end_time at time zone 'IST') daily, sum(time_elapsed)/60 
from pomodoros 
where status='completed' 
group by daily 
order by daily;			
			
select date_trunc('day', end_time) daily, sum(time_elapsed)/60
from pomodoros 
group by daily 
order by daily;

select date_trunc('week', end_time) weekly, sum(time_elapsed)/60
from pomodoros 
group by weekly 
order by weekly;


with data as (
  select date_trunc('day', end_time) as daily, sum(time_elapsed)/60 sum_time
  from pomodoros
  where status='completed'
  group by daily
  order by daily
)
select daily, avg(sum_time) over (order by daily asc rows between unbounded preceding and current row)
from data;

select date_trunc('day', end_time) as daily, avg(sum(time_elapsed)/60) over (order by date_trunc('day', end_time) asc rows between unbounded preceding and current row) 
from pomodoros 
where status='completed'
group by daily;


-- Task list
select t.id id, t.priority priority, t.description description, t.status status, t.dueDate dueDate, t.pomodoroLength pomodoroLength, 
sum(p.timeElapsed) pomodorosTimeElapsed, 
pr project
from tasks t
left join t.pomodoros p 
join projects pr on t.project.id = pr.id
where t.user.id = :user_id and t.project.id = :project_id and t.status = :status
and p.status in ('completed', 'past')
group by t.id, pr.id
order by t.priority asc, t.id desc
limit :limit offset :offset
