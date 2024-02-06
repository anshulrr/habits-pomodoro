package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("select p, p.projectCategory from projects p where p.user.id = :userId and p.id = :projectId")
	public Optional<Project> findUserProjectById(Long userId, Long projectId);

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
			select p.id id, p.name name, p.color color, p.description description, p.priority priority, p.pomodoroLength pomodoroLength, p.type type, p.dailyLimit dailyLimit, p.projectCategory.name category, p.projectCategory.color categoryColor
			from projects p 
			where p.user.id = :userId and status = :status
			order by p.projectCategory.level asc, p.priority asc, id desc 
			limit :limit offset :offset
			""")
	public List<ProjectForList> findUserProjects(Long userId, String status, int limit, int offset);
	
	@Query("""
			select p.id id, p.name name, p.type type, p.pomodoroLength pomodoroLength, p.dailyLimit dailyLimit
			from projects p 
			where p.user.id = :userId and p.projectCategory.id = :categoryId
			order by p.priority asc, id desc 
			""")
	public List<ProjectForList> findCategoryProjects(Long userId, Long categoryId);

	@Query(value = "select count(*) from projects where user_id = :userId and status = :status", nativeQuery = true)
	public Integer getUserProjectsCount(Long userId, String status);
}
