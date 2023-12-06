package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;

import com.anshul.atomichabits.dto.TaskDto;
import com.anshul.atomichabits.dto.TaskFilter;
import com.anshul.atomichabits.dto.TaskForList;

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

@Service
@Slf4j
@AllArgsConstructor
public class TaskService {

	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;
	private TagRepository tagRepository;
	
	public Task retriveTask(Long user_id, Long task_id) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + task_id);
		
		return taskEntry.get();
	}
	
	public List<TaskForList> retrieveAllTasks(Long user_id, int limit, int offset, TaskFilter filter, String status) {
		List<TaskForList> tasks;
		if (filter.projectId() != null) {			
			tasks = taskRepository.retrieveUserTasksByProjectId(user_id, filter.projectId(), status, limit, offset);
		} else if (filter.tagId() != null) {
			tasks = taskRepository.findTasksByUserIdAndTagsId(user_id, filter.tagId(), status, limit, offset);
		} else {
			tasks = taskRepository.retrieveFilteredTasks(user_id, status, filter.startDate(), filter.endDate(), limit, offset);
		}
		log.trace("tasks: {}", tasks);
		return tasks;
	}
	
	public Integer retrieveTasksCount(Long user_id, TaskFilter filter, String status) {
		Integer count = 0;
		log.debug("{} {} {}", status, filter.startDate(), filter.endDate());
		if (filter.projectId() != null) {	
			count = taskRepository.getProjectTasksCount(user_id, filter.projectId(), status);
		} else if (filter.tagId() != null) {
			count = taskRepository.getTagsTasksCount(user_id, filter.tagId(), status);
		} else {
			count = taskRepository.getFilteredTasksCount(user_id, status, filter.startDate(), filter.endDate());
		}
		return count;
	}
	
	public Task createTask(Long user_id, Long projectId, Task task) {
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, projectId);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + projectId);
		log.trace("found project: {}", projectEntry);

		task.setUser(userEntry.get());
		task.setProject(projectEntry.get());
		return taskRepository.save(task);
	}
	
	public Task updateTask(Long user_id, Long id, TaskDto taskDto) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + id);

		taskEntry.get().setDescription(taskDto.description());
		taskEntry.get().setPomodoroLength(taskDto.pomodoroLength());
		taskEntry.get().setDueDate(taskDto.dueDate());
		taskEntry.get().setStatus(taskDto.status());
		taskEntry.get().setPriority(taskDto.priority());
		taskEntry.get().setRepeatDays(taskDto.repeatDays());
		return taskRepository.save(taskEntry.get());
	}
	
	public Task addTag(Long user_id, Long id, List<Long> tagIds) {
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + id);
		
		Set<Tag> tags = tagRepository.findUserTagByIds(user_id, tagIds);
		
		taskEntry.get().setTags(tags);
		
	    return taskRepository.save(taskEntry.get());
	}
	
	public List<Object> retrieveTasksTags(long[] taskIds) {
		return taskRepository.findTaskTagsByIds(taskIds);
	}
	
	public List<Object> retrieveTasksTimeElapsed(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] taskIds) {
		log.debug(startDate + " " + endDate);
		List<Object> tasksTimeElapsed = taskRepository.findTasksTimeElapsed(user_id, startDate, endDate, taskIds);
		return tasksTimeElapsed;
	}
}
