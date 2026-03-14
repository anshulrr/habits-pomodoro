package com.anshul.atomichabits.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.ProjectCategory;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

	@Query("select p from project_categories p where p.user.id = :userId and p.id = :categoryId")
	public Optional<ProjectCategory> findUserProjectCategoryById(Long userId, UUID categoryId);

	@Query(value = "select * from project_categories where user_id = :userId and updated_at > :lastSyncTime order by level, id limit :limit offset :offset", nativeQuery = true)
	public List<ProjectCategory> findUserProjectCategories(Long userId, int limit, int offset, Instant lastSyncTime);
	
	@Query(value = "select * from project_categories where user_id = :userId and visible_to_partners = true order by level, id limit :limit offset :offset", nativeQuery = true)
	public List<ProjectCategory> findSubjectProjectCategories(Long userId, int limit, int offset);

	@Query(value = "select count(*) from project_categories where user_id = :userId", nativeQuery = true)
	public Integer getUserProjectCategoriesCount(Long userId);
}
