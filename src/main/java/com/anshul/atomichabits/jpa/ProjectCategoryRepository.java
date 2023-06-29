package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

	@Query("select p from project_categories p where p.user = ?1 and p.id = ?2")
	public Optional<ProjectCategory> findUserProjectCategoryById(User user, Long category_id);
	
	@Query(value = "select * from project_categories where user_id = ?1 order by id limit ?2 offset ?3", nativeQuery = true)
	public List<ProjectCategory> findUserProjectCategories(Long id, int limit, int offset);

	@Query(value = "select count(*) from project_categories where user_id = ?1", nativeQuery = true)
	public Integer getUserProjectCategoriesCount(Long id);
}
