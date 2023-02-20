package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.User;

import java.time.LocalDateTime;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

	@Query("select p from pomodoros p where p.user = ?1 and p.id = ?2")
	public Optional<Pomodoro> findUserPomodoroById(User user, Long task_id);
	
	@Query("select p from pomodoros p where p.user.id = ?1 and p.startTime >= ?2")
	public List<Pomodoro> findAllForToday(Long id, LocalDateTime date);
}
