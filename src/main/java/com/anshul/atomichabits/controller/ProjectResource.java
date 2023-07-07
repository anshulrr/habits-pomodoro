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

import com.anshul.atomichabits.dto.ProjectForList;
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
public class ProjectResource {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	

	public ProjectResource(UserRepository u, ProjectRepository p, ProjectCategoryRepository pc) {
		this.userRepository = u;
		this.projectRepository = p;
		this.projectCategoryRepository = pc;
	}
	
	@GetMapping("/projects/{id}")
	public Project retrieveProject(@PathVariable Long id, Principal principal) {
//		Optional<Project> project = projectRepository.findById(id);
//		
//		if (project.isEmpty())
//			throw new ProjectNotFoundException("id:" + id);
//		
//		if (!project.get().getUser().getUsername().equals(principal.getName()))
//			throw new NotAuthorizedException("not authorized");
		
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Project> project= projectRepository.findUserProjectById(user.get(), id);
		
		return project.get();
	}

	@GetMapping("/projects")
	public List<ProjectForList> retrieveProjectsOfUser(Principal principal, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
//		TODO: how to avoid user query for user id
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
//		System.out.println(principal + " " + principal.getClass());
		
//		TODO: using PageRequest
		List<ProjectForList> projects = projectRepository.findUserProjects(user.get(), limit, offset);
//		System.out.println(projects.get(0));
		
//		TODO: remove unnecessary data of project categories
		return projects;
	}
	
	@GetMapping("/projects/count")
	public Integer retrieveProjectsCountOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		return projectRepository.getUserProjectsCount(user.get().getId());
	}
	
	@PostMapping("/project-categories/{categoryId}/projects")
	public Project createProjectOfUser(@PathVariable Long categoryId, @Valid @RequestBody Project project, Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		Optional<ProjectCategory> category = projectCategoryRepository.findUserProjectCategoryById(user.get(), categoryId);
		
		project.setUser(user.get());
		
		project.setProjectCategory(category.get());
		
		return projectRepository.save(project);
	}
	
	@PutMapping("/projects/{id}")
	public Project updateProjectOfUser(@PathVariable Long id, @Valid @RequestBody Project project, Principal principal) {
		Optional<Project> projectEntry = projectRepository.findById(id);
		
		if (projectEntry.isEmpty())
			throw new ProjectNotFoundException("id:" + id);
		
		if (!projectEntry.get().getUser().getUsername().equals(principal.getName()))
			throw new NotAuthorizedException("not authorized");
		
		projectEntry.get().setName(project.getName());
		
		projectEntry.get().setDescription(project.getDescription());
		
		projectEntry.get().setColor(project.getColor());
		
		return projectRepository.save(projectEntry.get());
	}
}
