package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.model.Pomodoro;

import java.time.OffsetDateTime;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

	@Query("select p from pomodoros p where p.user.id = :userId and p.id = :id")
	public Optional<Pomodoro> findUserPomodoroById(Long userId, Long id);

	@Query("""
			select p.id id, p.status status, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.length length, 
			p.task task, p.task.project project 
			from pomodoros p 
			where p.user.id = :userId and p.status in ('started', 'paused')
			order by id desc
			""")
	public List<PomodoroDto> findRunningPomodoros(Long userId);

	@Query("""
			select p.id id, p.status status, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.task.id taskId, p.task.description task, p.task.project.color color, p.task.project.id projectId
			from pomodoros p
			where p.user.id = :userId and p.endTime >= :start and p.endTime <= :end and p.status in ('completed', 'past') and p.task.project.projectCategory.id in (:categories)
			order by p.endTime desc, p.id desc
			""")
	public List<PomodoroForList> findAllForToday(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories);
	
	@Query("""
			select p
			from pomodoros p 
			where p.user.id = :userId and p.task.id = :taskId and p.status in ('completed', 'past')
			order by p.endTime desc, p.id
			limit :limit offset :offset
			""")
	public List<Object> findTaskPomodoros(Long userId, Long taskId, int limit, int offset);
	
	@Query(value = "select count(*) from pomodoros where user_id = :userId and task_id = :taskId and status in ('completed', 'past')", nativeQuery = true)
	public Integer getTaskPomodorosCount(Long userId, Long taskId);
	
	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.project.projectCategory.name as category, p.task.project.projectCategory.color as color
			from pomodoros as p
			where p.user.id = :userId and p.status in ('completed', 'past') and endTime >= :start and endTime <= :end and p.task.project.projectCategory.id in (:categories)
			group by p.task.project.projectCategory.name, p.task.project.projectCategory.color, p.task.project.projectCategory.level
			order by p.task.project.projectCategory.level asc
			""")
	public List<Object> findProjectCategoriesTime(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories);
	
	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.project.name as project, p.task.project.color as color
			from pomodoros as p
			where p.user.id = :userId and p.status in ('completed', 'past') and endTime >= :start and endTime <= :end and p.task.project.projectCategory.id in (:categories)
			group by p.task.project.name, p.task.project.color, p.task.project.projectCategory.level, p.task.project.priority
			order by p.task.project.projectCategory.level asc, p.task.project.priority asc
			""")
	public List<Object> findProjectsTime(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories);

	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.description as task, p.task.project.color as color, p.task.project.name as project
			from pomodoros as p
			where p.user.id = :userId and p.status in ('completed', 'past') and endTime >= :start and endTime <= :end and p.task.project.projectCategory.id in (:categories)
			group by p.task.description, p.task.project.color, p.task.project.name, p.task.project.projectCategory.level, p.task.project.priority
			order by p.task.project.projectCategory.level asc, p.task.project.priority asc
			""")
	public List<Object> findTasksTime(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories);

	@Query(value = """
			select to_char(p.end_time at time zone :timezone, :limit) as date, sum(p.time_elapsed) / 60 as time, pp.id as id, pp.name as entity, pp.color as color, pc.level as level1, pp.priority as level2
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end and pc.id in (:categories)
			group by pp.name, pp.color, date, pc.level, pp.priority, pp.id
			order by date, id
			""", nativeQuery = true)
	public List<String[]> findTotalTime(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories, String timezone, String limit);

	@Query(value = """
			select to_char(p.end_time at time zone :timezone, :limit) as date, sum(p.time_elapsed) / 60 as time, t.id as id, t.description as entity, pp.color as color, pc.level as level1, pp.priority as level2, t.priority as level3
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end and pc.id in (:categories)
			group by pp.name, pp.color, date, pc.level, pp.priority, t.description, t.id, t.priority
			order by date, id
			""", nativeQuery = true)
	public List<String[]> findTasksTotalTime(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories, String timezone, String limit);
	
	@Query(value = """
			select sum(p.time_elapsed) / 60 as time, (p.end_time at time zone :timezone)::::date as pomodoro_date
			from pomodoros as p 
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end and pc.id in (:categories)
			group by pomodoro_date
			""", nativeQuery = true)
	public List<Object> findPomodorosCount(Long userId, OffsetDateTime start, OffsetDateTime end, long[] categories, String timezone);
	
	@Query(value = """
			select sum(p.time_elapsed) / 60 as time, (p.end_time at time zone :timezone)::::date as pomodoro_date
			from pomodoros as p 
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and pc.id = :categoryId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end
			group by pomodoro_date
			""", nativeQuery = true)
	public List<Object> findCategoryPomodorosCount(Long userId, Long categoryId, OffsetDateTime start, OffsetDateTime end, String timezone);
	
	@Query(value = """
			select sum(p.time_elapsed) / 60 as time, (p.end_time at time zone :timezone)::::date as pomodoro_date
			from pomodoros as p 
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and pp.id = :projectId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end
			group by pomodoro_date
			""", nativeQuery = true)
	public List<Object> findProjectPomodorosCount(Long userId, Long projectId, OffsetDateTime start, OffsetDateTime end, String timezone);
	
	@Query(value = """
			select sum(p.time_elapsed) / 60 as time, (p.end_time at time zone :timezone)::::date as pomodoro_date
			from pomodoros as p 
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :userId and t.id = :taskId and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end
			group by pomodoro_date
			""", nativeQuery = true)
	public List<Object> findTaskPomodorosCount(Long userId, Long taskId, OffsetDateTime start, OffsetDateTime end, String timezone);
}