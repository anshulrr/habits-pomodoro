package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class ProjectCategoryResource {

	private UserRepository userRepository;
	private ProjectCategoryRepository projectCategoryRepository;

	public ProjectCategoryResource(UserRepository u, ProjectCategoryRepository p) {
		this.userRepository = u;
		this.projectCategoryRepository = p;
	}

	@GetMapping("/project-categories/{id}")
	public ProjectCategory retrieveProject(@PathVariable Long id, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository
				.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);

		return categoryEntry.get();
	}

	@GetMapping("/project-categories")
	public List<ProjectCategory> retrieveProjectsOfUser(Principal principal,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryRepository.findUserProjectCategories(user_id, limit, offset);
	}

	@GetMapping("/project-categories/count")
	public Integer retrieveProjectsCountOfUser(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryRepository.getUserProjectCategoriesCount(user_id);
	}

	@PostMapping("/project-categories")
	public ProjectCategory createProjectOfUser(@Valid @RequestBody ProjectCategory projectCategory,
			Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);

		projectCategory.setUser(userEntry.get());
		return projectCategoryRepository.save(projectCategory);
	}

	@PutMapping("/project-categories/{id}")
	public ProjectCategory createProjectOfUser(@PathVariable Long id,
			@Valid @RequestBody ProjectCategory projectCategory, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);

		categoryEntry.get().setName(projectCategory.getName());
		categoryEntry.get().setLevel(projectCategory.getLevel());
		categoryEntry.get().setStatsDefault(projectCategory.isStatsDefault());
		return projectCategoryRepository.save(categoryEntry.get());
	}
}
