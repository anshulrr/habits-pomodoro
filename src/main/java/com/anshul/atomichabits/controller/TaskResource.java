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
import com.anshul.atomichabits.dto.TaskDto;
import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TaskResource {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;

	public TaskResource(UserRepository u, ProjectRepository p, TaskRepository t) {
		this.userRepository = u;
		this.projectRepository = p;
		this.taskRepository = t;
	}

	@GetMapping("/projects/{project_id}/tasks/{task_id}")
	public Task retrieveProject(@PathVariable Long project_id, @PathVariable Long task_id, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + task_id);
		
		return taskEntry.get();
	}

	@GetMapping("/projects/{project_id}/tasks")
	public List<TaskForList> retrieveProjectsOfUser(@PathVariable Long project_id, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		List<TaskForList> tasks = taskRepository.retrieveUserTasksByProjectId(user_id, project_id, status);
		log.trace("tasks: {}", tasks);
		return tasks;
	}

	@PostMapping("/projects/{project_id}/tasks")
	public Task retrieveProjectsOfUser(@PathVariable Long project_id, @RequestBody Task task, Principal principal) {
		// log.trace("task for entry: " + project_id + task);
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, project_id);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + project_id);
		log.trace("found project: {}", projectEntry);

		task.setUser(userEntry.get());
		task.setProject(projectEntry.get());
		return taskRepository.save(task);
	}

	@PutMapping("/projects/{project_id}/tasks/{id}")
	public Task retrieveProjectsOfUser(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + id);

		taskEntry.get().setDescription(taskDto.description());
		taskEntry.get().setPomodoroLength(taskDto.pomodoroLength());
		taskEntry.get().setStatus(taskDto.status());
		return taskRepository.save(taskEntry.get());
	}
}
