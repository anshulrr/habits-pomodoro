package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;

import com.anshul.atomichabits.dto.TaskDto;
import com.anshul.atomichabits.dto.TaskFilter;
import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.jpa.CommentRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Tag;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class TaskService {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;
	private TagRepository tagRepository;
	private CommentRepository commentRepository;
	
	private static final String NOT_FOUND_MESSAGE = "task id:";
	
	public Task retriveTask(Long userId, Long taskId) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, taskId);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException(NOT_FOUND_MESSAGE + taskId);
		
		return taskEntry.get();
	}
	
	public List<TaskForList> retrieveAllTasks(Long userId, int limit, int offset, TaskFilter filter, String status) {
		List<TaskForList> tasks;
		if (filter.projectId() != null) {			
			tasks = taskRepository.retrieveUserTasksByProjectId(userId, filter.projectId(), status, limit, offset);
		} else if (filter.tagId() != null) {
			tasks = taskRepository.findTasksByUserIdAndTagsId(userId, filter.tagId(), status, limit, offset);
		} else if (filter.startDate() != null) {
			tasks = taskRepository.retrieveFilteredTasks(userId, status, filter.startDate(), filter.endDate(), limit, offset);
		} else {
			tasks = taskRepository.retrieveSearchedTasks(userId, status, filter.searchString(), limit, offset);
		}
		log.trace("tasks: {}", tasks);
		return tasks;
	}
	
	public Integer retrieveTasksCount(Long userId, TaskFilter filter, String status) {
		Integer count = 0;
		log.debug("{} {} {}", status, filter.startDate(), filter.endDate());
		if (filter.projectId() != null) {	
			count = taskRepository.getProjectTasksCount(userId, filter.projectId(), status);
		} else if (filter.tagId() != null) {
			count = taskRepository.getTagsTasksCount(userId, filter.tagId(), status);
		} else if (filter.startDate() != null) {
			count = taskRepository.getFilteredTasksCount(userId, status, filter.startDate(), filter.endDate());
		} else {
			count = taskRepository.getSearchedTasksCount(userId, status, filter.searchString());
		}
		return count;
	}
	
	public Task createTask(Long userId, Long projectId, Task task) {
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(userId, projectId);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + projectId);
		log.trace("found project: {}", projectEntry);

		task.setUser(userEntry.get());
		task.setProject(projectEntry.get());
		return taskRepository.save(task);
	}
	
	@Transactional
	public Task updateTask(Long userId, Long id, TaskDto taskDto) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException(NOT_FOUND_MESSAGE + id);
		
		Optional<Project> projectEntry = projectRepository.findUserProjectById(userId, taskDto.projectId());
		if (projectEntry.isEmpty())
			throw new ResourceNotFoundException("project id:" + id);
		
		// for switch project
		if (!taskEntry.get().getProject().getId().equals(projectEntry.get().getId())) {			
			// handle comments table for project and category update
			// @Transactional and @Modifying is required for update query
			commentRepository.updateCommentsProjectAndCategory(userId, id, projectEntry.get().getId(), projectEntry.get().getProjectCategory().getId());
		}

		taskEntry.get().setDescription(taskDto.description());
		taskEntry.get().setPomodoroLength(taskDto.pomodoroLength());
		taskEntry.get().setDueDate(taskDto.dueDate());
		taskEntry.get().setStatus(taskDto.status());
		taskEntry.get().setType(taskDto.type());
		taskEntry.get().setPriority(taskDto.priority());
		taskEntry.get().setRepeatDays(taskDto.repeatDays());
		taskEntry.get().setDailyLimit(taskDto.dailyLimit());
		taskEntry.get().setEnableNotifications(taskDto.enableNotifications());
		taskEntry.get().setProject(projectEntry.get());
		return taskRepository.save(taskEntry.get());
	}
	
	@Transactional
	public Task updateTaskPriority(Long userId, Long id, Map<String, String> map) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException(NOT_FOUND_MESSAGE + id);
		
		Integer prevOrder = Optional.ofNullable(map.get("prevOrder"))
                				.filter(s -> !s.isEmpty())
                				.map(Integer::parseInt)
                				.orElse(null);
		Integer nextOrder = Optional.ofNullable(map.get("nextOrder"))
                				.filter(s -> !s.isEmpty())
                				.map(Integer::parseInt)
                				.orElse(null);
		
		// handle start or end of the list
		Integer priority;
		if (prevOrder == null) {
			priority = nextOrder - 1000;
		} else if (nextOrder == null) {
			priority = prevOrder + 1000;
		} else {			
			priority = (prevOrder + nextOrder) / 2;
		}
		log.debug("{} {} {}", priority, prevOrder, nextOrder);
		
		taskEntry.get().setPriority(priority);

		return taskRepository.save(taskEntry.get());
	}
	
	@Transactional
	public Boolean resetProjectTaskPriority(Long userId, Long projectId) {
		log.info("updating project's {} task's orders", projectId);
		taskRepository.updateTasksPriorityOrder(userId, projectId);
		return true;
	}
	
	public Task addTag(Long userId, Long id, List<Long> tagIds) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException(NOT_FOUND_MESSAGE + id);
		
		Set<Tag> tags = tagRepository.findUserTagByIds(userId, tagIds);
		
		taskEntry.get().setTags(tags);
		
	    return taskRepository.save(taskEntry.get());
	}
	
	public List<Object> retrieveTasksTags(long[] taskIds) {
		return taskRepository.findTaskTagsByIds(taskIds);
	}
	
	public List<Object> retrieveTasksTimeElapsed(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, long[] taskIds) {
		log.debug(startDate + " " + endDate);
		return taskRepository.findTasksTimeElapsed(userId, startDate, endDate, taskIds);
	}
	
	public List<Object> retrieveTasksCommentsCount(long[] taskIds) {
		return taskRepository.countTaskCommentsByIds(taskIds);
	}
}
