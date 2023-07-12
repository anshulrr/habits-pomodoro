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
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class TaskResource {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;

	public TaskResource(UserRepository u, ProjectRepository p, TaskRepository t) {
		this.userRepository = u;
		this.projectRepository = p;
		this.taskRepository = t;
	}

	//	@GetMapping("/projects/{project_id}/tasks/{task_id}")
	//	public Project retrieveProject(@PathVariable Long project_id, @PathVariable Long task_id, Principal principal) {
	// Optional<User> user = userRepository.findByUsername(principal.getName());
	// 
	// Optional<Project> project = projectRepository.findUserProjectById(user.get(), project_id);
	// 
	// return project.get();
	//	}

	@GetMapping("/projects/{project_id}/tasks")
	public List<Task> retrieveProjectsOfUser(@PathVariable Long project_id, Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		List<Task> tasks = taskRepository.retrieveTasksByProjectId(user.get(), project_id);

		return tasks;
	}

	@PostMapping("/projects/{project_id}/tasks")
	public Task retrieveProjectsOfUser(@PathVariable Long project_id, @RequestBody Task task, Principal principal) {
		// System.out.println("" + project_id + task);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Project> project = projectRepository.findUserProjectById(user.get(), project_id);

		// System.out.println(project);

		task.setUser(user.get());
		task.setProject(project.get());

		return taskRepository.save(task);
	}
}
