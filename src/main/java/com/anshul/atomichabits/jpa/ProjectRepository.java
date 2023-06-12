package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("select p from projects p where p.user = ?1 and p.id = ?2")
	public Optional<Project> findUserProjectById(User user, Long project_id);
	
	@Query(value = "select * from projects where user_id = ?1 order by id limit ?2 offset ?3", nativeQuery = true)
	public List<Project> findUserProjects(Long id, int limit, int offset);
}
