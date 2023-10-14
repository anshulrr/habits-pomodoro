package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.web.server.ResponseStatusException;

import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.dto.PomodoroUpdateDto;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ExtendWith(MockitoExtension.class)
class RunningPomodoroTest {

	@InjectMocks
	private PomodoroService pomodoroService;

	@Mock
	private UserRepository userRepositoryMock;
	
	@Mock
	private ProjectCategoryRepository projectCategoryRepositoryMock;

	@Mock
	private ProjectRepository projectRepositoryMock;
	
	@Mock
	private TaskRepository taskRepositoryMock;
	
	@Mock
	private PomodoroRepository pomodoroRepositoryMock;
	
	@Mock
	private TagRepository tagRepositoryMock;
	
	static User user;
	static ProjectCategory category;
	static Project project;
	static Task task;
	
	static Long USER_ID = 1L;
	static Long CATEGORY_ID = 11L;
	static Long PROJECT_ID = 22L;
	static Long TASK_ID = 33L;
	static Long POMODORO_ID = 44L;
	
	@BeforeAll
	static void setup() {
		user = new User("Samay", "samay@xyz.com");
		category = new ProjectCategory(CATEGORY_ID, "Sample Category", user);
		project = new Project(PROJECT_ID, "Sample Project", user, category);
		task = new Task(TASK_ID, "Test Task", user, project);
	}
	
	@Test
	void udpateStatusFromStartedToPaused() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("paused");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("paused", updatedPomodoro.getStatus());
		assertEquals(minutes * 60, updatedPomodoro.getTimeElapsed());
	}
	
	@Test
	void udpateStatusFromStartedToCompleted() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(Long.valueOf(minutes));
		OffsetDateTime endDate = OffsetDateTime.now(ZoneOffset.UTC); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("completed");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("completed", updatedPomodoro.getStatus());
		assertEquals(minutes * 60, updatedPomodoro.getTimeElapsed());
		// TODO: find better way
		assertEquals(0, Duration.between(endDate, updatedPomodoro.getEndTime()).getSeconds());
	}
	
	@Test
	void udpateStatusFromStartedToStarted() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("started");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("started", updatedPomodoro.getStatus());
		assertEquals(minutes * 60, updatedPomodoro.getTimeElapsed());
	}
	
	@Test
	void udpateStartedPomodoroData() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updatePomodoroData();
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("started", updatedPomodoro.getStatus());
		assertEquals(minutes * 60, updatedPomodoro.getTimeElapsed());
	}
	
	@Test
	void udpateStatusFromPausedToStarted() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 100, "paused", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("started");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("started", updatedPomodoro.getStatus());
		assertEquals(100, updatedPomodoro.getTimeElapsed());
	}

	@Test
	void udpateStatusFromPausedToPaused() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 100, "paused", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("paused");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("paused", updatedPomodoro.getStatus());
		assertEquals(100, updatedPomodoro.getTimeElapsed());
	}

	@Test
	void udpateStatusFromPausedToCompleted() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 100, "paused", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("completed");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("completed", updatedPomodoro.getStatus());
		assertEquals(100, updatedPomodoro.getTimeElapsed());
	}
	
	@Test
	void udpatePausedPomodoroData() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 100, "started", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updatePomodoroData();
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("started", updatedPomodoro.getStatus());
		assertEquals(minutes * 60, updatedPomodoro.getTimeElapsed());
	}

	@Test
	void udpateStatusFromCompletedToStarted() {
		Integer minutes = 5;
		OffsetDateTime startDate = OffsetDateTime.now().minusMinutes(Long.valueOf(minutes)); 
		OffsetDateTime endDate = OffsetDateTime.now(); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, endDate, 100, "completed", task, user);
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoro);
		
		runningPomodoro.updateStatus("completed");
		
		Pomodoro updatedPomodoro = runningPomodoro.getPomodoro();
		
		assertEquals("completed", updatedPomodoro.getStatus());
		assertEquals(100, updatedPomodoro.getTimeElapsed());
	}
}