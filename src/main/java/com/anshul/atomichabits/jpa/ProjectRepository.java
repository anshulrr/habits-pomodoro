package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("select p from Project p where p.user = ?1 and p.id = ?2")
	public Optional<Project> findByProjectId(User user, Long project_id);
}
