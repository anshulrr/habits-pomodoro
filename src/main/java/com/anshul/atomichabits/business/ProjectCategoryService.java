package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

@Service
public class ProjectCategoryService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectCategoryRepository projectCategoryRepository;
	
	public ProjectCategory retriveProjectCategory(Long user_id, Long id) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);
		return categoryEntry.get();
	}
	
	public List<ProjectCategory> retrieveAllProjectCategories(Long user_id, Integer limit, Integer offset) {
		return projectCategoryRepository.findUserProjectCategories(user_id, limit, offset);
	}
	
	public Integer retrieveAllProjectCategoriesCount(Long user_id) {
		return projectCategoryRepository.getUserProjectCategoriesCount(user_id);
	}
	
	public ProjectCategory createProjectCategory(Long user_id, ProjectCategory projectCategory) {
		Optional<User> userEntry = userRepository.findById(user_id);

		projectCategory.setUser(userEntry.get());
		
		return projectCategoryRepository.save(projectCategory);
	}
	
	public ProjectCategory updateProjectCategory(Long user_id, Long id, ProjectCategory projectCategory) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);
		
		categoryEntry.get().setName(projectCategory.getName());
		categoryEntry.get().setLevel(projectCategory.getLevel());
		categoryEntry.get().setStatsDefault(projectCategory.isStatsDefault());
		return projectCategoryRepository.save(categoryEntry.get());
	}	
}
