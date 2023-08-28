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

import com.anshul.atomichabits.dto.ProjectDto;
import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
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
	public ProjectDto retrieveProject(@PathVariable Long id, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, id);
		if (projectEntry.isEmpty())
			 	throw new ResourceNotFoundException("project id:" + id);
		
		return new ProjectDto(projectEntry.get());
	}

	@GetMapping("/projects")
	public List<ProjectForList> retrieveProjectsOfUser(Principal principal,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());

		// TODO: using PageRequest
		List<ProjectForList> projects = projectRepository.findUserProjects(user_id, limit, offset);
		log.trace("first project: {}", projects.get(0));

		// TODO: remove unnecessary data of project categories
		return projects;
	}

	@GetMapping("/projects/count")
	public Integer retrieveProjectsCountOfUser(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return projectRepository.getUserProjectsCount(user_id);
	}

	@PostMapping("/projects")
	public ProjectDto createProjectOfUser(@Valid @RequestBody ProjectDto projectDto,
			Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);

		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id,
				projectDto.getProjectCategoryId());
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + projectDto.getProjectCategoryId());

		Project project = new Project();

		project.setName(projectDto.getName());
		project.setDescription(projectDto.getDescription());
		project.setColor(projectDto.getColor());
		project.setPomodoroLength(projectDto.getPomodoroLength());
		project.setPriority(projectDto.getPriority());
		project.setUser(userEntry.get());
		project.setProjectCategory(categoryEntry.get());
		projectRepository.save(project);

		return new ProjectDto(project);
	}

	@PutMapping("/projects/{id}")
	public Project updateProjectOfUser(@PathVariable Long id,
			@Valid @RequestBody ProjectDto projectDto, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, id);

		Optional<ProjectCategory> category = projectCategoryRepository.findUserProjectCategoryById(user_id,
				projectDto.getProjectCategoryId());
		if (projectEntry.isEmpty())
			throw new ResourceNotFoundException("project id:" + id);
		log.trace("project for update: " + projectEntry + projectDto + category);

		projectEntry.get().setName(projectDto.getName());
		projectEntry.get().setDescription(projectDto.getDescription());
		projectEntry.get().setColor(projectDto.getColor());
		projectEntry.get().setPomodoroLength(projectDto.getPomodoroLength());
		projectEntry.get().setPriority(projectDto.getPriority());
		projectEntry.get().setProjectCategory(category.get());
		projectRepository.save(projectEntry.get());
		log.trace("updated project: {}", projectEntry);

		return projectEntry.get();
	}
}
