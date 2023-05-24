package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.User;

import java.time.OffsetDateTime;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

	@Query("select p from pomodoros p where p.user = ?1 and p.id = ?2")
	public Optional<Pomodoro> findUserPomodoroById(User user, Long task_id);
	
	@Query("select p from pomodoros p where p.user.id = ?1 and p.startTime >= ?2 and p.status = 'completed'")
	public List<Pomodoro> findAllForToday(Long id, OffsetDateTime date);
	
//	@Query("select count(p) from pomodoros p where p.user.id = ?1 and p.status = 'completed'")
//	public int findAllCount(Long id, OffsetDateTime date);
	
	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.project.name as project, p.task.project.color as color
			from pomodoros as p
			where p.user.id=?1 and p.status='completed' and endTime >= ?2
			group by p.task.project.name, p.task.project.color 
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findProjectsTime(Long id, OffsetDateTime date);
	
	@Query("""
			select sum(p.timeElapsed) / 60 as time, p.task.description as task, p.task.project.color as color
			from pomodoros as p
			where p.user.id=?1 and p.status='completed' and endTime >= ?2
			group by p.task.description, p.task.project.color 
			order by sum(p.timeElapsed) desc
			""")
	public List<Object> findTasksTime(Long id, OffsetDateTime date);
}
