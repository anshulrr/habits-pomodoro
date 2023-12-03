package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.ProjectCategory;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

	@Query("select p from project_categories p where p.user.id = :user_id and p.id = :category_id")
	public Optional<ProjectCategory> findUserProjectCategoryById(Long user_id, Long category_id);

	@Query(value = "select * from project_categories where user_id = :user_id order by level limit :limit offset :offset", nativeQuery = true)
	public List<ProjectCategory> findUserProjectCategories(Long user_id, int limit, int offset);
	
	@Query(value = "select * from project_categories where user_id = :user_id and visible_to_partners = true order by level limit :limit offset :offset", nativeQuery = true)
	public List<ProjectCategory> findSubjectProjectCategories(Long user_id, int limit, int offset);

	@Query(value = "select count(*) from project_categories where user_id = :user_id", nativeQuery = true)
	public Integer getUserProjectCategoriesCount(Long user_id);
}
