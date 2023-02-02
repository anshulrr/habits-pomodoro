package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select p from tasks p where p.user = ?1 and p.id = ?2")
	public Optional<Task> findUserTaskById(User user, Long task_id);
}
