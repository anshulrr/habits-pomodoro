
package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/tasks/{task_id}")
	public Task retrieveTask(Principal principal, @PathVariable Long task_id) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + task_id);
		
		return taskEntry.get();
	}

	@GetMapping("/tasks")
	public List<TaskForList> retrieveTasks(Principal principal, 
			@RequestParam Long projectId, 
			@RequestParam(defaultValue = "added") String status, 
			@RequestParam(defaultValue = "") Instant startDate, 
			@RequestParam(defaultValue = "") Instant endDate,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		List<TaskForList> tasks;
		if (projectId != -1) {			
			tasks = taskRepository.retrieveUserTasksByProjectId(user_id, projectId, status, limit, offset);
		} else {
			tasks = taskRepository.retrieveFilteredTasks(user_id, status, startDate, endDate, limit, offset);
		}
		log.trace("tasks: {}", tasks);
		return tasks;
	}

	@GetMapping("/tasks/count")
	public Integer retrieveTasksCountOfUserProject(Principal principal, 
			@RequestParam Long projectId, 
			@RequestParam(defaultValue = "") Instant startDate, 
			@RequestParam(defaultValue = "") Instant endDate,
			@RequestParam(defaultValue = "added") String status) {
		Long user_id = Long.parseLong(principal.getName());
		Integer count = 0;
		log.debug("{} {} {}", status, startDate, endDate);
		if (projectId != -1) {	
			count = taskRepository.getProjectTasksCount(user_id, projectId, status);
		} else {
			count = taskRepository.getFilteredTasksCount(user_id, status, startDate, endDate);
		}
		return count;
	}

	@PostMapping("/tasks")
	public Task createTask(Principal principal, @RequestParam Long projectId, @RequestBody Task task) {
		// log.trace("task for entry: " + projectId + task);
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, projectId);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + projectId);
		log.trace("found project: {}", projectEntry);

		task.setUser(userEntry.get());
		task.setProject(projectEntry.get());
		return taskRepository.save(task);
	}

	@PutMapping("/tasks/{id}")
	public Task updateTask(Principal principal, @PathVariable Long id, @Valid @RequestBody TaskDto taskDto) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + id);

		taskEntry.get().setDescription(taskDto.description());
		taskEntry.get().setPomodoroLength(taskDto.pomodoroLength());
		taskEntry.get().setDueDate(taskDto.dueDate());
		taskEntry.get().setStatus(taskDto.status());
		taskEntry.get().setPriority(taskDto.priority());
		return taskRepository.save(taskEntry.get());
	}

//	@GetMapping("/tasks")
//	public List<TaskForList> retrieveFilteredTasks(Principal principal, 
//			@RequestParam(defaultValue = "added") String status, 
//			@RequestParam OffsetDateTime startDate, 
//			@RequestParam OffsetDateTime endDate,
//			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
//		Long user_id = Long.parseLong(principal.getName());
//		List<TaskForList> tasks = taskRepository.retrieveFilteredTasks(user_id, status, startDate, endDate, limit, offset);
//		log.trace("tasks: {}", tasks);
//		return tasks;
//	}
//
//	@GetMapping("/tasks/count")
//	public Integer retrieveFilteredTasksCount(Principal principal, @RequestParam(defaultValue = "added") String status, @RequestParam OffsetDateTime startDate, @RequestParam OffsetDateTime endDate) {
//		Long user_id = Long.parseLong(principal.getName());
//		return taskRepository.getFilteredTasksCount(user_id, status, startDate, endDate);
//	}
}

record TaskFilter(OffsetDateTime startDate, OffsetDateTime endDate) {}
