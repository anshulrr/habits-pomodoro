package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = ?1 and t.id = ?2")
	public Optional<Task> findUserTaskById(Long user_id, Long id);

	@Query("select t from tasks t where t.user.id = ?1 and t.project.id = ?2 and status = ?3 order by id desc")
	public List<Task> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status);
}
