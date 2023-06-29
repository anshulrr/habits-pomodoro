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
	
//	@Query(value = "select * from projects where user_id = ?1 order by id limit ?2 offset ?3", nativeQuery = true)
//	@Query(value = "select p.id, p.description, p.color, p.name, p.pomodoro_length, p.project_category_id, p.user_id, pc.name category from projects p join project_categories pc on p.project_category_id = pc.id where p.user_id = ?1 order by p.id limit ?2 offset ?3", nativeQuery = true)
//	@Query(value = "select p.*, pc.name category from projects p join project_categories pc on p.project_category_id = pc.id where p.user_id = ?1 order by p.id limit ?2 offset ?3", nativeQuery = true)
	@Query(value = "select p from projects p JOIN FETCH p.projectCategory where p.user = ?1 order by p.id limit ?2 offset ?3")
	public List<Project> findUserProjects(User user, int limit, int offset);

	@Query(value = "select count(*) from projects where user_id = ?1", nativeQuery = true)
	public Integer getUserProjectsCount(Long id);
}
