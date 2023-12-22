package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.AccountabilityPartnerService;
import com.anshul.atomichabits.business.TaskService;
import com.anshul.atomichabits.dto.TaskDto;
import com.anshul.atomichabits.dto.TaskFilter;
import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.model.Task;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TaskResource {

	private TaskService taskService;

	private AccountabilityPartnerService accountabilityPartnerService;
	
	@GetMapping("/tasks/{task_id}")
	public Task retrieveTask(Principal principal, @PathVariable Long task_id) {
		Long user_id = Long.parseLong(principal.getName());
		return taskService.retriveTask(user_id, task_id);
	}

	@GetMapping("/tasks")
	public ResponseEntity<List<TaskForList>> retrieveTasks(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) Long projectId, 
			@RequestParam(required = false) Instant startDate, 
			@RequestParam(required = false) Instant endDate,
			@RequestParam(required = false) Long tagId,
			@RequestParam(defaultValue = "current") String status, 
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId != null) {
			if (accountabilityPartnerService.isSubject(user_id, subjectId)) {
				user_id = subjectId;
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		TaskFilter filter = new TaskFilter(projectId, tagId, startDate, endDate);
		return new ResponseEntity<>(taskService.retrieveAllTasks(user_id, limit, offset, filter, status), HttpStatus.OK);
	}

	@GetMapping("/tasks/count")
	public Integer retrieveTasksCountOfUserProject(Principal principal, 
			@RequestParam(required = false) Long projectId, 
			@RequestParam(required = false) Instant startDate, 
			@RequestParam(required = false) Instant endDate,
			@RequestParam(required = false) Long tagId,
			@RequestParam(defaultValue = "current") String status) {
		Long user_id = Long.parseLong(principal.getName());
		TaskFilter filter = new TaskFilter(projectId, tagId, startDate, endDate);
		return taskService.retrieveTasksCount(user_id, filter, status);
	}

	@PostMapping("/tasks")
	public Task createTask(Principal principal, @RequestParam Long projectId, @RequestBody Task task) {
		Long user_id = Long.parseLong(principal.getName());
		return taskService.createTask(user_id, projectId, task);
	}

	@PutMapping("/tasks/{id}")
	public Task updateTask(Principal principal, @PathVariable Long id, @Valid @RequestBody TaskDto taskDto) {
		Long user_id = Long.parseLong(principal.getName());
		return taskService.updateTask(user_id, id, taskDto);
	}
	
	@PostMapping("/tasks/{id}/tags")
	public ResponseEntity<Task> addTag(Principal principal, 
			@PathVariable Long id, 
			@RequestBody MapTagsRequest request) {
		Long user_id = Long.parseLong(principal.getName());
	    return new ResponseEntity<>(taskService.addTag(user_id, id, request.tagIds()), HttpStatus.CREATED);
	}
	
	@GetMapping("/tasks/tags")
	public List<Object> retrieveTasksTags(Principal principal, 
			@RequestParam("taskIds") long[] taskIds) {
		return taskService.retrieveTasksTags(taskIds);
	}
	
	@GetMapping("tasks/pomodoros/time-elapsed")
	public List<Object> retrieveTasksTimeElapsed(Principal principal, 
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate,
			@RequestParam("taskIds") long[] taskIds) {
		Long user_id = Long.parseLong(principal.getName());
		return taskService.retrieveTasksTimeElapsed(user_id, startDate, endDate, taskIds);
	}
}

record MapTagsRequest(List<Long> tagIds) {}