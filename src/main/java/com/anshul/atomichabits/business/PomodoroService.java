package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.dto.PomodoroUpdateDto;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.jpa.UserSettingsRepository;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;
import com.anshul.atomichabits.model.UserSettings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class PomodoroService {

	private UserRepository userRepository;
	private TaskRepository taskRepository;
	private UserSettingsRepository userSettingsRepository;
	private PomodoroRepository pomodoroRepository;
	
	public List<PomodoroForList> retrievePomodoros(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		log.debug(startDate + " " + endDate);
		List<PomodoroForList> pomodoros = pomodoroRepository.findAllForToday(userId, startDate, endDate, categories);
		log.trace("first pomodoro: {}", !pomodoros.isEmpty() ? pomodoros.get(0).getId() : "nill");
		return pomodoros;
	}
	
	public Pomodoro createPomodoro(Long userId, Long taskId, Pomodoro pomodoro) {
		//Check if there is any running pomodoro for the user
		List<PomodoroDto> runningPomodoros= pomodoroRepository.findRunningPomodoros(userId);
		if (!runningPomodoros.isEmpty()) {
			log.trace("running pomodoro: {}", runningPomodoros.get(0));
			throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
		}

		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, taskId);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + taskId);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		pomodoro.setLength(calculateAndSetPomodoroLength(userId, taskEntry.get()));
		log.debug("pomodoro {}", pomodoro);

		return pomodoroRepository.save(pomodoro);
	}
	
	public Pomodoro createPastPomodoro(Long userId, Long taskId, Pomodoro pomodoro) {
		//Check if there is any running pomodoro for the user
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, taskId);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + taskId);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		pomodoro.setLength(calculateAndSetPomodoroLength(userId, taskEntry.get()));
		pomodoro.setStatus("past");				
		log.debug("pomodoro {}", pomodoro);
		
		if (pomodoro.getTimeElapsed() > pomodoro.getLength() * 60) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		}

		return pomodoroRepository.save(pomodoro);
	}
	
	private Integer calculateAndSetPomodoroLength(Long userId, Task task) {		
		Integer length;
		
		// check task settings, then project settings, then user settings
		Integer taskPomodoroLength = task.getPomodoroLength();
		if (taskPomodoroLength != 0) {
			length = taskPomodoroLength;
			log.debug("task length {}", length);
		} else {
			Integer projectPomodoroLength = task.getProject().getPomodoroLength();
			if (projectPomodoroLength != 0) {
				length = projectPomodoroLength;
				log.debug("project length {}", length);
			} else {
				// TODO: get length from user settings stored in auth context
				UserSettings settings = userSettingsRepository.findUserSettings(userId);
				length = settings.getPomodoroLength();
				log.debug("settings length {}", length);
			}
		}
		
		return length;
	}
	
	public Pomodoro deletePomodoro(Long userId, Long id) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(userId, id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);
		
		pomodoroEntry.get().setStatus("deleted");
		
		return pomodoroRepository.save(pomodoroEntry.get());
	}
	
	public Pomodoro updatePomodoro(Long userId, Long id, PomodoroUpdateDto pomodoroUpdateDto) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(userId, id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);
		log.debug("new pomodoro request: {}, pomodoro entry: {}", pomodoroUpdateDto, pomodoroEntry.get());
		// TODO: log if timeElapsed calculated in front-end and back-end has large difference
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updateStatus(pomodoroUpdateDto.status());
		
		return pomodoroRepository.save(runningPomodoro.getPomodoro());
	}
	
	public PomodoroDto getRunningPomodoro(Long userId) {
		List<PomodoroDto> runningPomodoros = pomodoroRepository.findRunningPomodoros(userId);
		if (runningPomodoros.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);
		} else if (runningPomodoros.size() > 1) {
			// handle if more than one entry found
			// TODO: find better solution
			log.info("multiple pomodoros found: {}", runningPomodoros.size());
			for (int i = 1; i < runningPomodoros.size(); i++) {
				pomodoroRepository.deleteById(runningPomodoros.get(i).getId());
			}
		}
		
		// update pomodoro data for running pomodoro
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(userId, runningPomodoros.get(0).getId());
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updatePomodoroData();
		pomodoroRepository.save(runningPomodoro.getPomodoro());
		
		if (runningPomodoro.getPomodoro().getStatus() == "completed") {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);
		}
		
		List<PomodoroDto> updatedRunningPomodoroEntry = pomodoroRepository.findRunningPomodoros(userId);
		return updatedRunningPomodoroEntry.get(0);
	}
}
