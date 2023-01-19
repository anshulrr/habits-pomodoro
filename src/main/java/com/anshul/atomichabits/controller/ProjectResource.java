package com.anshul.atomichabits.controller;

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
	
	@GetMapping("/users/{user_id}/projects/{project_id}")
	public Project retrieveProject(@PathVariable Long user_id, @PathVariable Long project_id) {
		Optional<User> user = userRepository.findById(user_id);
		Optional<Project> project = projectRepository.findByProjectId(user.get(), project_id);
		
		return project.get();
	}

	@GetMapping("/users/{id}/projects")
	public List<Project> retrieveProjectsOfUser(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);

		return user.get().getProjects();
	}
	
	@PostMapping("/users/{id}/projects")
	public Project retrieveProjectsOfUser(@PathVariable Long id, @Valid @RequestBody Project project) {
		
		Optional<User> user = userRepository.findById(id);
		
		project.setUser(user.get());
		
		projectRepository.save(project);
		
		return project;
	}
}
