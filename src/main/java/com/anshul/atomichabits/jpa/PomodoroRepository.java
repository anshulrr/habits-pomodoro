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

	@Query("select p from pomodoros p where p.user.id = ?1 and p.id = ?2")
	public Optional<Pomodoro> findUserPomodoroById(Long user_id, Long task_id);

	@Query("""
			select p.id id, p.status status, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.length length, 
			p.task task, p.task.project project 
			from pomodoros p 
			where p.user.id = ?1 and p.status != 'completed'
			""")
	public Optional<PomodoroDto> findRunningPomodoro(Long user_id);

	@Query("""
			select p.id id, p.startTime startTime, p.endTime endTime, p.timeElapsed timeElapsed, p.task.description task
			from pomodoros p
			where p.user.id = ?1 and p.endTime >= ?2 and p.endTime <= ?3 and p.status = 'completed' and p.task.project.projectCategory.id in (?4)
			order by p.id desc
			""")
	public List<PomodoroForList> findAllForToday(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories);

	//	@Query("select count(p) from pomodoros p where p.user.id = ?1 and p.status = 'completed'")
	//	public int findAllCount(Long id, OffsetDateTime date);

	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.project.name as project, p.task.project.color as color
			from pomodoros as p
			where p.user.id=?1 and p.status='completed' and endTime >= ?2 and endTime <= ?3 and p.task.project.projectCategory.id in (?4)
			group by p.task.project.name, p.task.project.color
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findProjectsTime(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories);

	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.description as task, p.task.project.color as color
			from pomodoros as p
			where p.user.id=?1 and p.status='completed' and endTime >= ?2 and endTime <= ?3 and p.task.project.projectCategory.id in (?4)
			group by p.task.description, p.task.project.color
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findTasksTime(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories);

	@Query(value = """
			select to_char(p.end_time at time zone 'ASIA/KOLKATA', 'DD'), sum(p.time_elapsed) / 60 as time, pp.name as project, pp.color as color
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id=?1 and p.status='completed' and end_time >= ?2 and end_time <= ?3 and pc.id in (?4)
			group by pp.name, pp.color, to_char(p.end_time at time zone 'ASIA/KOLKATA', 'DD')
			order by to_char(p.end_time at time zone 'ASIA/KOLKATA', 'DD'), pp.name
			""", nativeQuery = true)
	public List<String[]> findTotalTimeDaily(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories, String timezone);

	@Query(value = """
			select to_char(p.end_time at time zone 'ASIA/KOLKATA', 'WW'), sum(p.time_elapsed) / 60 as time, pp.name as project, pp.color as color
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id=?1 and p.status='completed' and end_time >= ?2 and end_time <= ?3 and pc.id in (?4)
			group by pp.name, pp.color, to_char(p.end_time at time zone 'ASIA/KOLKATA', 'WW')
			order by to_char(p.end_time at time zone 'ASIA/KOLKATA', 'WW'), pp.name
			""", nativeQuery = true)
	public List<String[]> findTotalTimeWeekly(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories, String timezone);

	@Query(value = """
			select to_char(p.end_time at time zone 'ASIA/KOLKATA', 'MM'), sum(p.time_elapsed) / 60 as time, pp.name as project, pp.color as color
			from pomodoros as p
			join tasks as t on p.task_id = t.id
			join projects as pp on t.project_id = pp.id
			join project_categories as pc on pp.project_category_id = pc.id
			where p.user_id=?1 and p.status='completed' and end_time >= ?2 and end_time <= ?3 and pc.id in (?4)
			group by pp.name, pp.color, to_char(p.end_time at time zone 'ASIA/KOLKATA', 'MM')
			order by to_char(p.end_time at time zone 'ASIA/KOLKATA', 'MM'), pp.name
			""", nativeQuery = true)
	public List<String[]> findTotalTimeMonthly(Long id, OffsetDateTime date, OffsetDateTime end, long[] categories, String timezone);

}