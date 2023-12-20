package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.anshul.atomichabits.dto.TaskDto;
import com.anshul.atomichabits.dto.TaskFilter;
import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.Tag;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@InjectMocks
	private TaskService taskService;

	@Mock
	private UserRepository userRepositoryMock;
	
	@Mock
	private ProjectCategoryRepository projectCategoryRepositoryMock;

	@Mock
	private ProjectRepository projectRepositoryMock;
	
	@Mock
	private TaskRepository taskRepositoryMock;
	
	@Mock
	private TagRepository tagRepositoryMock;
	
	static User user;
	static ProjectCategory category;
	static Project project;
	
	static Long USER_ID = 1L;
	static Long CATEGORY_ID = 11L;
	static Long PROJECT_ID = 111L;
	static Long TASK_ID = 111L;
	static Long TAG_ID = 1111L;
	
	@BeforeAll
	static void setup() {
		user = new User("Samay", "samay@xyz.com");
		category = new ProjectCategory(CATEGORY_ID, "Sample Category", user);
		project = new Project(PROJECT_ID, "Sample Project", user, category);
	}
	
	@Test
	void retriveTask() {
		Task task = new Task(TASK_ID, "Test Task", user, project);
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, TASK_ID))
			.thenReturn(Optional.of(task));
		
		Task taskResult = taskService.retriveTask(USER_ID, TASK_ID);
		
		assertNotNull(taskResult);
	}
	
	@Test
	void retriveTaskEmpty() {
		Long nil_task_id = 12L;
		when(taskRepositoryMock.findUserTaskById(USER_ID, nil_task_id))
			.thenReturn(Optional.ofNullable(null));
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			taskService.retriveTask(1L, nil_task_id);
	    });
		
	    assertEquals("task id:" + nil_task_id, exception.getMessage());
	}
	
	@Test
	void retrieveAllProjectTasks() {
		String status = "added";
		TaskFilter filter = new TaskFilter(PROJECT_ID, null, null, null);
		
		when(taskRepositoryMock.retrieveUserTasksByProjectId(USER_ID, PROJECT_ID, status, 0, 0))
			.thenReturn(new ArrayList<TaskForList>());
		
		List<TaskForList> tasks = taskService.retrieveAllTasks(USER_ID, 0, 0, filter, status);
		
		assertEquals(0, tasks.size());
	}
	
	@Test
	void retrieveAllTagTasks() {
		String status = "added";
		TaskFilter filter = new TaskFilter(null, TAG_ID, null, null);
		
		when(taskRepositoryMock.findTasksByUserIdAndTagsId(USER_ID, TAG_ID, status, 0, 0))
			.thenReturn(new ArrayList<TaskForList>());
		
		List<TaskForList> tasks = taskService.retrieveAllTasks(USER_ID, 0, 0, filter, status);
		
		assertEquals(0, tasks.size());
	}
	
	@Test
	void retrieveAllFilteredTasks() {
		String status = "added";
		Instant startDate = Instant.now(); 
		Instant endDate = Instant.now();
		TaskFilter filter = new TaskFilter(null, null, startDate, endDate);
		
		when(taskRepositoryMock.retrieveFilteredTasks(USER_ID, status, startDate, endDate, 0, 0))
			.thenReturn(new ArrayList<TaskForList>());
		
		List<TaskForList> tasks = taskService.retrieveAllTasks(USER_ID, 0, 0, filter, status);
		
		assertEquals(0, tasks.size());
	}
	
	@Test
	void retrieveAllProjectTasksCount() {
		String status = "added";
		TaskFilter filter = new TaskFilter(PROJECT_ID, null, null, null);
		
		when(taskRepositoryMock.getProjectTasksCount(USER_ID, PROJECT_ID, status))
			.thenReturn(1);
		
		Integer tasksCount = taskService.retrieveTasksCount(USER_ID, filter, status);
		
		assertEquals(1, tasksCount);
	}
	
	@Test
	void retrieveAllTagsTasksCount() {
		String status = "added";
		TaskFilter filter = new TaskFilter(null, TAG_ID, null, null);
		
		when(taskRepositoryMock.getTagsTasksCount(USER_ID, TAG_ID, status))
			.thenReturn(2);
		
		Integer tasksCount = taskService.retrieveTasksCount(USER_ID, filter, status);
		
		assertEquals(2, tasksCount);
	}
	
	@Test
	void retrieveAllFilteredTasksCount() {
		String status = "added";
		Instant startDate = Instant.now(); 
		Instant endDate = Instant.now();
		TaskFilter filter = new TaskFilter(null, null, startDate, endDate);
		
		when(taskRepositoryMock.getFilteredTasksCount(USER_ID, status, startDate, endDate))
			.thenReturn(3);
		
		Integer tasksCount = taskService.retrieveTasksCount(USER_ID, filter, status);
		
		assertEquals(3, tasksCount);
	}
	
	@Test
	void createTask() {
		when(userRepositoryMock.findById(1L))
			.thenReturn(Optional.of(user));
		
		when(projectRepositoryMock.findUserProjectById(USER_ID, PROJECT_ID))
			.thenReturn(Optional.of(project));
		
		Task taskRequest = new Task(TASK_ID, "Test Task", user, project);
		
		taskService.createTask(USER_ID, PROJECT_ID, taskRequest);
		
		ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
	}
	
	@Test
	void updateTask() {
		Task task = new Task(TASK_ID, "Test Task", user, project);
		String status = "added";
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, TASK_ID))
			.thenReturn(Optional.of(task));
		
		TaskDto taskDtoRequest = new TaskDto(TASK_ID, "Test Task", 25, null, 0, 5, 1, status, "neutral", PROJECT_ID);
		
		taskService.updateTask(USER_ID, TASK_ID, taskDtoRequest);
		
		ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepositoryMock).save(captor.capture());
		
		assertEquals(task, captor.getValue());
		assertEquals(5, captor.getValue().getPriority());
	}
	
	@Test
	void updateTaskEmpty() {
		Long nil_task_id = 12L;
		String status = "added";
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, nil_task_id))
			.thenReturn(Optional.ofNullable(null));
		
		TaskDto taskDtoRequest = new TaskDto(TASK_ID, "Test Task", 25, null, 0, 5, 1, status, "neutral", PROJECT_ID);
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			taskService.updateTask(USER_ID, nil_task_id, taskDtoRequest);
	    });
	    assertEquals("task id:" + nil_task_id, exception.getMessage());
	}
	
	@Test
	void addTag() {
		Task task = new Task(TASK_ID, "Test Task", user, project);
		
		Tag tag = new Tag(TAG_ID, "Test Tag", user);
		
		List<Long> tagIds = new ArrayList<>();
		tagIds.add(TAG_ID);
		
		Set<Tag> tags = new HashSet<>();
		tags.add(tag);
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, TASK_ID))
			.thenReturn(Optional.of(task));
		
		when(tagRepositoryMock.findUserTagByIds(USER_ID, tagIds))
		.thenReturn(tags);
		
		taskService.addTag(USER_ID, TASK_ID, tagIds);
		
		assertEquals(task.getTags(), tags);
	}
	
	@Test
	void retrieveTasksTags() {
		long[] taskIds = new long[]{ TASK_ID };
		
		when(taskRepositoryMock.findTaskTagsByIds(taskIds))
			.thenReturn(new ArrayList<Object>());
		
		List<Object> result = taskService.retrieveTasksTags(taskIds);
		
		assertEquals(result.size(), 0);
	}
	
	@Test
	void retrieveTasksTimeElapsed() {
		long[] taskIds = new long[]{ TASK_ID };
		OffsetDateTime startDate = OffsetDateTime.now(); 
		OffsetDateTime endDate = OffsetDateTime.now();
		
		when(taskRepositoryMock.findTasksTimeElapsed(USER_ID, startDate, endDate, taskIds))
			.thenReturn(new ArrayList<Object>());
		
		List<Object> result = taskService.retrieveTasksTimeElapsed(USER_ID, startDate, endDate, taskIds);
		
		assertEquals(result.size(), 0);
	}
}
