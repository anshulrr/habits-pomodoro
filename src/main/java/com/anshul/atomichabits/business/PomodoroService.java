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

@Service
@Slf4j
@AllArgsConstructor
public class PomodoroService {

	private UserRepository userRepository;
	private TaskRepository taskRepository;
	private UserSettingsRepository userSettingsRepository;
	private PomodoroRepository pomodoroRepository;
	
	public List<PomodoroForList> retrievePomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		// TODO: using PageRequest
		log.debug(startDate + " " + endDate);
		List<PomodoroForList> pomodoros = pomodoroRepository.findAllForToday(user_id, startDate, endDate, categories);
		log.trace("first pomodoro: {}", pomodoros.size() != 0 ? pomodoros.get(0).getId() : "nill");
		return pomodoros;
	}
	
	public Pomodoro createPomodoro(Long user_id, Long task_id, Pomodoro pomodoro) {
		//Check if there is any running pomodoro for the user
		List<PomodoroDto> runningPomodoros= pomodoroRepository.findRunningPomodoros(user_id);
		if (runningPomodoros.size() != 0) {
			log.trace("running pomodoro: {}", runningPomodoros.get(0));
			throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
		}

		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + task_id);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		pomodoro.setLength(calculateAndSetPomodoroLength(user_id, taskEntry.get()));
		log.debug("pomodoro {}", pomodoro);

		return pomodoroRepository.save(pomodoro);
	}
	
	public Pomodoro createPastPomodoro(Long user_id, Long task_id, Pomodoro pomodoro) {
		//Check if there is any running pomodoro for the user
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + task_id);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		pomodoro.setLength(calculateAndSetPomodoroLength(user_id, taskEntry.get()));
		pomodoro.setStatus("past");				
		log.debug("pomodoro {}", pomodoro);
		
		if (pomodoro.getTimeElapsed() > pomodoro.getLength() * 60) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		}

		return pomodoroRepository.save(pomodoro);
	}
	
	private Integer calculateAndSetPomodoroLength(Long user_id, Task task) {		
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
				UserSettings settings = userSettingsRepository.findUserSettings(user_id);
				length = settings.getPomodoroLength();
				log.debug("settings length {}", length);
			}
		}
		
		return length;
	}
	
	public Pomodoro deletePastPomodoro(Long user_id, Long id) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id, id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);
		
		pomodoroEntry.get().setStatus("deleted");
		
		return pomodoroRepository.save(pomodoroEntry.get());
	}
	
	public Pomodoro updatePomodoro(Long user_id, Long id, PomodoroUpdateDto pomodoroUpdateDto) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id, id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);
		log.debug("new pomodoro request: {}, pomodoro entry: {}", pomodoroUpdateDto, pomodoroEntry.get());
		// TODO: log if timeElapsed calculated in front-end and back-end has large difference
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updateStatus(pomodoroUpdateDto.status());
		
		return pomodoroRepository.save(runningPomodoro.getPomodoro());
	}
	
	public PomodoroDto getRunningPomodoro(Long user_id) {
		List<PomodoroDto> runningPomodoros = pomodoroRepository.findRunningPomodoros(user_id);
		if (runningPomodoros.size() == 0) {
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
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id, runningPomodoros.get(0).getId());
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updatePomodoroData();
		pomodoroRepository.save(runningPomodoro.getPomodoro());
		
		if (runningPomodoro.getPomodoro().getStatus() == "completed") {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);
		}
		
		List<PomodoroDto> updatedRunningPomodoroEntry = pomodoroRepository.findRunningPomodoros(user_id);
		return updatedRunningPomodoroEntry.get(0);
	}
}
