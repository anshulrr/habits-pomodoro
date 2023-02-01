package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class ProjectResource {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	

	public ProjectResource(UserRepository u, ProjectRepository p) {
		this.userRepository = u;
		this.projectRepository = p;
	}
	
	@GetMapping("/projects/{id}")
	public Project retrieveProject(@PathVariable Long id, Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		Optional<Project> project = projectRepository.findUserProjectById(user.get(), id);
		
		if (project.isEmpty())
			throw new ProjectNotFoundException("id:" + id);
		
		return project.get();
	}

	@GetMapping("/projects")
	public List<Project> retrieveProjectsOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		return user.get().getProjects();
	}
	
	@PostMapping("/projects")
	public Project retrieveProjectsOfUser(@Valid @RequestBody Project project, Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		project.setUser(user.get());
		
		projectRepository.save(project);
		
		return project;
	}
}
