package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

@Service
@CacheConfig(cacheNames = "projectCategoryCache")
public class ProjectCategoryService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectCategoryRepository projectCategoryRepository;
	
	@Cacheable(value = "projectCategory", key = "#id", unless = "#result == null")
	public ProjectCategory retriveProjectCategory(Long user_id, Long id) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);
		return categoryEntry.get();
	}
	
	@Cacheable(value = "projectCategories")
	public List<ProjectCategory> retrieveAllProjectCategories(Long user_id, Integer limit, Integer offset) {
		return projectCategoryRepository.findUserProjectCategories(user_id, limit, offset);
	}
	
	public List<ProjectCategory> retrieveSubjectProjectCategories(Long subjectId, Integer limit, Integer offset) {
		return projectCategoryRepository.findSubjectProjectCategories(subjectId, limit, offset);
	}
	
	public Integer retrieveAllProjectCategoriesCount(Long user_id) {
		return projectCategoryRepository.getUserProjectCategoriesCount(user_id);
	}
	
	@CacheEvict(cacheNames = "projectCategories", allEntries = true)
	public ProjectCategory createProjectCategory(Long user_id, ProjectCategory projectCategory) {
		Optional<User> userEntry = userRepository.findById(user_id);

		projectCategory.setUser(userEntry.get());

		return projectCategoryRepository.save(projectCategory);
	}
	
	@Caching(evict = { @CacheEvict(cacheNames = "projectCategory", key = "#id"),
			@CacheEvict(cacheNames = "projectCategories", allEntries = true) })
	public ProjectCategory updateProjectCategory(Long user_id, Long id, ProjectCategory projectCategory) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);

		categoryEntry.get().setName(projectCategory.getName());
		categoryEntry.get().setLevel(projectCategory.getLevel());
		categoryEntry.get().setColor(projectCategory.getColor());
		categoryEntry.get().setStatsDefault(projectCategory.isStatsDefault());
		categoryEntry.get().setVisibleToPartners(projectCategory.isVisibleToPartners());
		return projectCategoryRepository.save(categoryEntry.get());
	}	
}
