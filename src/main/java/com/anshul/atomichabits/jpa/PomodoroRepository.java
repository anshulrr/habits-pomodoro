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

	@Query("select p from pomodoros p where p.user.id = :user_id and p.id = :id")
	public Optional<Pomodoro> findUserPomodoroById(Long user_id, Long id);

	@Query("""
			select p.id id, p.status status, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.length length, 
			p.task task, p.task.project project 
			from pomodoros p 
			where p.user.id = :user_id and p.status in ('started', 'paused')
			""")
	public Optional<PomodoroDto> findRunningPomodoro(Long user_id);

	@Query("""
			select p.id id, p.status status, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.task.description task
			from pomodoros p
			where p.user.id = :user_id and p.endTime >= :start and p.endTime <= :end and p.status in ('completed', 'past') and p.task.project.projectCategory.id in (:categories)
			order by p.endTime desc, p.id desc
			""")
	public List<PomodoroForList> findAllForToday(Long user_id, OffsetDateTime start, OffsetDateTime end, long[] categories);

	//	@Query("select count(p) from pomodoros p where p.user.id = ?1 and p.status = 'completed'")
	//	public int findAllCount(Long id, OffsetDateTime date);

	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.project.name as project, p.task.project.color as color
			from pomodoros as p
			where p.user.id = :user_id and p.status in ('completed', 'past') and endTime >= :start and endTime <= :end and p.task.project.projectCategory.id in (:categories)
			group by p.task.project.name, p.task.project.color
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findProjectsTime(Long user_id, OffsetDateTime start, OffsetDateTime end, long[] categories);

	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.description as task, p.task.project.color as color
			from pomodoros as p
			where p.user.id = :user_id and p.status in ('completed', 'past') and endTime >= :start and endTime <= :end and p.task.project.projectCategory.id in (:categories)
			group by p.task.description, p.task.project.color
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findTasksTime(Long user_id, OffsetDateTime start, OffsetDateTime end, long[] categories);

	@Query(value = """
			select to_char(p.end_time at time zone :timezone, :limit) as date, sum(p.time_elapsed) / 60 as time, pp.name as project, pp.color as color
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id = :user_id and p.status in ('completed', 'past') and end_time >= :start and end_time <= :end and pc.id in (:categories)
			group by pp.name, pp.color, date
			order by date, pp.name
			""", nativeQuery = true)
	public List<String[]> findTotalTime(Long user_id, OffsetDateTime start, OffsetDateTime end, long[] categories, String timezone, String limit);
}