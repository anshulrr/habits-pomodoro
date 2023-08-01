package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("select p, p.projectCategory from projects p where p.user.id = ?1 and p.id = ?2")
	public Optional<Project> findUserProjectById(Long user_id, Long project_id);

	//	@Query(value = "select * from projects where user_id = ?1 order by id limit ?2 offset ?3", nativeQuery = true)
	//	@Query(value = "select p.id, p.description, p.color, p.name, p.pomodoro_length, p.project_category_id, p.user_id, pc.name category from projects p join project_categories pc on p.project_category_id = pc.id where p.user_id = ?1 order by p.id limit ?2 offset ?3", nativeQuery = true)
	//	@Query(value = """
	//		select p.*, pc.name category 
	//		from projects p 
	//		join project_categories pc on p.project_category_id = pc.id 
	//		where p.user_id = ?1 
	//		order by p.id 
	//		limit ?2 offset ?3
	//		""", nativeQuery = true)
	@Query("""
			select p.id id, p.name name, p.color color, p.pomodoroLength pomodoroLength, p.projectCategory.name category 
			from projects p 
			where p.user.id = ?1 
			order by p.projectCategory.level asc, id desc 
			limit ?2 offset ?3
			""")
	public List<ProjectForList> findUserProjects(Long user_id, int limit, int offset);

	@Query(value = "select count(*) from projects where user_id = ?1", nativeQuery = true)
	public Integer getUserProjectsCount(Long id);
}
