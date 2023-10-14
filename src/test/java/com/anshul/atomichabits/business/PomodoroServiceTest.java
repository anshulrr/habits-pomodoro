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

import java.time.OffsetDateTime;
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
class PomodoroServiceTest {

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
	void retrievePomodoros() {
		long[] categoryIds = new long[]{ CATEGORY_ID };
		OffsetDateTime startDate = OffsetDateTime.now(); 
		OffsetDateTime endDate = OffsetDateTime.now();
		
		when(pomodoroRepositoryMock.findAllForToday(USER_ID, startDate, endDate, categoryIds))
			.thenReturn(new ArrayList<PomodoroForList>());
		
		List<PomodoroForList> pomodoros = pomodoroService.retrievePomodoros(USER_ID, startDate, endDate, categoryIds);
		
		assertEquals(0, pomodoros.size());
	}
	
	@Test
	void createPomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		Pomodoro pomodoroRequest = new Pomodoro(null, startDate, null, null, null, null, null);
		task.setPomodoroLength(20);
		
		when(userRepositoryMock.findById(1L))
			.thenReturn(Optional.of(user));
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, TASK_ID))
			.thenReturn(Optional.of(task));
		
		pomodoroService.createPomodoro(USER_ID, TASK_ID, pomodoroRequest);
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
	}
	
	@Test
	void createPomodotoNotAllowed() {
		when(pomodoroRepositoryMock.findRunningPomodoros(USER_ID))
			.thenReturn(new ArrayList<PomodoroDto>());
		
		OffsetDateTime startDate = OffsetDateTime.now(); 
		Pomodoro pomodoroRequest = new Pomodoro(null, startDate, null, null, null, null, null);
		
		List<PomodoroDto> runningPomodoros = new ArrayList<PomodoroDto>();
		PomodoroDto pomodoroDto = new PomodoroDtoImpl(POMODORO_ID, startDate, null, 0, 0, "started", task, project);
		runningPomodoros.add(pomodoroDto);
		
		when(pomodoroRepositoryMock.findRunningPomodoros(USER_ID))
			.thenReturn(runningPomodoros);
		
		Exception exception = assertThrows(ResponseStatusException.class, () -> {
			pomodoroService.createPomodoro(USER_ID, TASK_ID, pomodoroRequest);
	    });
	    assertEquals("405 METHOD_NOT_ALLOWED", exception.getMessage());
	}
	
	@Test
	void createPastPomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		OffsetDateTime endDate = OffsetDateTime.now(); 
		Integer timeElapsed = 1200;
		Pomodoro pomodoroRequest = new Pomodoro(null, startDate, endDate, timeElapsed, null, null, null);
		task.setPomodoroLength(0);
		project.setPomodoroLength(20);
		
		when(userRepositoryMock.findById(1L))
			.thenReturn(Optional.of(user));
		
		when(taskRepositoryMock.findUserTaskById(USER_ID, TASK_ID))
			.thenReturn(Optional.of(task));
		
		pomodoroService.createPastPomodoro(USER_ID, TASK_ID, pomodoroRequest);
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
		assertEquals("past", captor.getValue().getStatus());
	}
	
	@Test
	void deletePastPomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		OffsetDateTime endDate = OffsetDateTime.now(); 
		Integer timeElapsed = 1200;
		String status = "past";
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, endDate, timeElapsed, status, null, null);
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, POMODORO_ID))
			.thenReturn(Optional.of(pomodoro));
		
		pomodoroService.deletePastPomodoro(USER_ID, POMODORO_ID);
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals("deleted", captor.getValue().getStatus());
	}
	
	@Test
	void deleteCompletedPastPomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		OffsetDateTime endDate = OffsetDateTime.now(); 
		Integer timeElapsed = 1200;
		String status = "completed";
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, endDate, timeElapsed, status, null, null);
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, POMODORO_ID))
			.thenReturn(Optional.of(pomodoro));
		
		Exception exception = assertThrows(ResponseStatusException.class, () -> {
			pomodoroService.deletePastPomodoro(USER_ID, POMODORO_ID);
	    });
	    assertEquals("401 UNAUTHORIZED", exception.getMessage());
	}
	
	@Test
	void updatePomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		
		String status = "paused";
		PomodoroUpdateDto pomodoroUpdateDto = new PomodoroUpdateDto(15, status);
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, POMODORO_ID))
			.thenReturn(Optional.of(pomodoro));
		
		pomodoroService.updatePomodoro(USER_ID, POMODORO_ID, pomodoroUpdateDto);
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
		assertEquals("paused", captor.getValue().getStatus());
	}
	
	@Test
	void updatePomodoroEmpty() {
		Long nil_pomodoro_id = 12L;
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, nil_pomodoro_id))
			.thenReturn(Optional.ofNullable(null));
		
		String status = "paused";
		PomodoroUpdateDto pomodoroUpdateDto = new PomodoroUpdateDto(15, status);
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			pomodoroService.updatePomodoro(USER_ID, nil_pomodoro_id, pomodoroUpdateDto);
	    });
	    assertEquals("pomodoro id:" + nil_pomodoro_id, exception.getMessage());
	}
	
	@Test
	void getRunningPomodotoEmpty() {
		when(pomodoroRepositoryMock.findRunningPomodoros(USER_ID))
			.thenReturn(new ArrayList<PomodoroDto>());
		
		Exception exception = assertThrows(ResponseStatusException.class, () -> {
			pomodoroService.getRunningPomodoro(USER_ID);
	    });
	    assertEquals("204 NO_CONTENT", exception.getMessage());
	}
	
	@Test
	void getRunningPomodoro() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		List<PomodoroDto> runningPomodoros = new ArrayList<PomodoroDto>();
		
		PomodoroDto pomodoroDto = new PomodoroDtoImpl(POMODORO_ID, startDate, null, 0, 0, "started", task, project);
		runningPomodoros.add(pomodoroDto);
		
		when(pomodoroRepositoryMock.findRunningPomodoros(USER_ID))
			.thenReturn(runningPomodoros);
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, POMODORO_ID))
			.thenReturn(Optional.of(pomodoro));
		
		pomodoroService.getRunningPomodoro(USER_ID);
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals(0, captor.getValue().getTimeElapsed());
	}
	
	@Test
	void getRunningPomodoroMultiple() {
		OffsetDateTime startDate = OffsetDateTime.now(); 
		Pomodoro pomodoro = new Pomodoro(POMODORO_ID, startDate, null, 0, "started", task, user);
		List<PomodoroDto> runningPomodoros = new ArrayList<PomodoroDto>();
		
		PomodoroDto pomodoroDto = new PomodoroDtoImpl(POMODORO_ID, startDate, null, 0, 0, "started", task, project);
		runningPomodoros.add(pomodoroDto);
		runningPomodoros.add(pomodoroDto);
		runningPomodoros.add(pomodoroDto);
		
		when(pomodoroRepositoryMock.findRunningPomodoros(USER_ID))
			.thenReturn(runningPomodoros);
		
		when(pomodoroRepositoryMock.findUserPomodoroById(USER_ID, POMODORO_ID))
			.thenReturn(Optional.of(pomodoro));
		
		pomodoroService.getRunningPomodoro(USER_ID);
		
		// check if multiple entries are deleted	
		verify(pomodoroRepositoryMock, times(2)).deleteById(anyLong());;
		
		ArgumentCaptor<Pomodoro> captor = ArgumentCaptor.forClass(Pomodoro.class);
		verify(pomodoroRepositoryMock).save(captor.capture());
		
		assertEquals(0, captor.getValue().getTimeElapsed());
	}
}

@Getter
@AllArgsConstructor
class PomodoroDtoImpl implements PomodoroDto {
	Long id;
	OffsetDateTime startTime;
	OffsetDateTime endTime;
	Integer timeElapsed;
	Integer length;
	String status;
	Task task;
	Project project;
}