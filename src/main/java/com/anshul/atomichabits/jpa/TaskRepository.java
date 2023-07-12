package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user = ?1 and t.id = ?2")
	public Optional<Task> findUserTaskById(User user, Long id);

	@Query("select t from tasks t where t.user = ?1 and t.project.id = ?2")
	public List<Task> retrieveTasksByProjectId(User user, Long project_id);
}
