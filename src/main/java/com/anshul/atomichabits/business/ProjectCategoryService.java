package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import com.anshul.atomichabits.dto.ProjectCategoryDto;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
@CacheConfig(cacheNames = "projectCategoryCache")
public class ProjectCategoryService {

	private UserRepository userRepository;

	private ProjectCategoryRepository projectCategoryRepository;
	
	@Cacheable(value = "projectCategory", key = "#id", unless = "#result == null")
	public ProjectCategory retriveProjectCategory(Long userId, Long id) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(userId, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);
		return categoryEntry.get();
	}
	
	@Cacheable(value = "projectCategories")
	public List<ProjectCategory> retrieveAllProjectCategories(Long userId, Integer limit, Integer offset) {
		return projectCategoryRepository.findUserProjectCategories(userId, limit, offset);
	}
	
	public List<ProjectCategory> retrieveSubjectProjectCategories(Long subjectId, Integer limit, Integer offset) {
		return projectCategoryRepository.findSubjectProjectCategories(subjectId, limit, offset);
	}
	
	public Integer retrieveAllProjectCategoriesCount(Long userId) {
		return projectCategoryRepository.getUserProjectCategoriesCount(userId);
	}
	
	@CacheEvict(cacheNames = "projectCategories", allEntries = true)
	public ProjectCategory createProjectCategory(Long userId, ProjectCategoryDto categoryDto) {
		Optional<User> userEntry = userRepository.findById(userId);
		
		ProjectCategory category = new ProjectCategory();

		category.setUser(userEntry.get());
		category.setName(categoryDto.getName());
		category.setColor(categoryDto.getColor());
		category.setLevel(categoryDto.getLevel());
		category.setStatsDefault(categoryDto.isStatsDefault());
		category.setVisibleToPartners(categoryDto.isVisibleToPartners());
		
		log.debug("{}", category);

		return projectCategoryRepository.save(category);
	}
	
	@Caching(evict = { @CacheEvict(cacheNames = "projectCategory", key = "#id"),
			@CacheEvict(cacheNames = "projectCategories", allEntries = true) })
	public ProjectCategory updateProjectCategory(Long userId, Long id, ProjectCategoryDto projectCategory) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(userId, id);
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
