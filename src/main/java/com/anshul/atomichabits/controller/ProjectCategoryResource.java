package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.exceptions.NotAuthorizedException;
import com.anshul.atomichabits.exceptions.ProjectNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
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
		Optional<ProjectCategory> project = projectCategoryRepository.findById(id);
		
		if (project.isEmpty())
			throw new ProjectNotFoundException("id:" + id);
		
		if (!project.get().getUser().getUsername().equals(principal.getName()))
			throw new NotAuthorizedException("not authorized");
		
		return project.get();
	}

	@GetMapping("/project-categories")
	public List<ProjectCategory> retrieveProjectsOfUser(Principal principal, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
//		TODO: how to avoid user query for user id
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		return projectCategoryRepository.findUserProjectCategories(user.get().getId(), limit, offset);
	}
	
	@GetMapping("/project-categories/count")
	public Integer retrieveProjectsCountOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		return projectCategoryRepository.getUserProjectCategoriesCount(user.get().getId());
	}
	
	@PostMapping("/project-categories")
	public ProjectCategory createProjectOfUser(@Valid @RequestBody ProjectCategory projectCategory, Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		projectCategory.setUser(user.get());
		
		projectCategoryRepository.save(projectCategory);
		
		return projectCategory;
	}
	
	@PutMapping("/project-categories/{id}")
	public ProjectCategory createProjectOfUser(@PathVariable Long id, @Valid @RequestBody ProjectCategory projectCategory, Principal principal) {
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findById(id);
		
		if (categoryEntry.isEmpty())
			throw new ProjectNotFoundException("id:" + id);
		
		if (!categoryEntry.get().getUser().getUsername().equals(principal.getName()))
			throw new NotAuthorizedException("not authorized");
		
		categoryEntry.get().setName(projectCategory.getName());
		
		categoryEntry.get().setLevel(projectCategory.getLevel());
		
		projectCategoryRepository.save(categoryEntry.get());
		
		return categoryEntry.get();
	}
}
