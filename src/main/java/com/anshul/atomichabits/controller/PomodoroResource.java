package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.RunningPomodoro;
import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.*;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;
import com.anshul.atomichabits.model.UserSettings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
public class PomodoroResource {

	private UserRepository userRepository;
	private PomodoroRepository pomodoroRepository;
	private TaskRepository taskRepository;
	private UserSettingsRepository userSettingsRepository;
	
	@GetMapping("/pomodoros")
	public List<PomodoroForList> retrievePomodorosOfUser(Principal principal, @RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		log.debug(startDate + " " + endDate);
		List<PomodoroForList> pomodoros = pomodoroRepository.findAllForToday(user_id, startDate, endDate, categories);
		log.trace("first pomodoro: {}", pomodoros.size() != 0 ? pomodoros.get(0).getId() : "nill");
		return pomodoros;
	}

	@GetMapping("/stats/projects-time")
	public List<Object> retrieveProjectPomodoros(Principal principal, @RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		List<Object> result = pomodoroRepository.findProjectsTime(user_id, startDate, endDate, categories);
		return result;
	}

	@GetMapping("/stats/tasks-time")
	public List<Object> retrieveTaskPomodoros(Principal principal, @RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		log.debug("" + startDate + " " + endDate);
		List<Object> result = pomodoroRepository.findTasksTime(user_id, startDate, endDate, categories);
		return result;
	}

	@GetMapping("/stats/total-time")
	public Map<String, List<String[]>> retrieveTotalPomodoros(Principal principal, @RequestParam String limit,
			@RequestParam OffsetDateTime startDate, @RequestParam OffsetDateTime endDate,
			@RequestParam("include_categories") long[] categories,
			@RequestParam(defaultValue = "UTC") String timezone) {
		Long user_id = Long.parseLong(principal.getName());

		limit = "" + Character.toUpperCase(limit.charAt(0));
		limit += limit;

		List<String[]> result = pomodoroRepository.findTotalTime(user_id, startDate, endDate, categories, timezone,
				limit);
		log.trace("total time result: {}", result);

		Map<String, List<String[]>> groupedResult = result.stream()
				.collect(Collectors.groupingBy((element -> (String) element[2])));
		log.trace("total time groupedResult: {}", groupedResult);

		return groupedResult;
	}

	@PostMapping("/pomodoros")
	public ResponseEntity<Pomodoro> createPomodoro(@Valid @RequestBody Pomodoro pomodoro, @RequestParam Long task_id,
			Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		//Check if there is any running pomodoro for the user
		Optional<PomodoroDto> runningPomodoroEntry = pomodoroRepository.findRunningPomodoro(user_id);

		if (runningPomodoroEntry.isPresent()) {
			log.trace("running pomodoro: {}", runningPomodoroEntry);
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}

		// log.trace(pomodoro.toString() + task_id);
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + task_id);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		log.trace("pomodoro for entry: {}", pomodoro);

		// TODO: get length from user settings stored in auth context
		UserSettings settings = userSettingsRepository.findUserSettings(user_id);
		pomodoro.setLength(settings.getPomodoroLength());

		Integer taskPomodoroLength = taskEntry.get().getPomodoroLength();
		if (taskPomodoroLength != 0) {
			pomodoro.setLength(taskPomodoroLength);
		} else {
			Integer projectPomodoroLength = taskEntry.get().getProject().getPomodoroLength();
			if (projectPomodoroLength != 0) {
				log.trace("setting length: " + projectPomodoroLength);
				pomodoro.setLength(projectPomodoroLength);
			}
		}
		log.trace("pomodoro length: {}", pomodoro.getLength());
		log.debug("pomodoro {}", pomodoro);

		return new ResponseEntity<>(pomodoroRepository.save(pomodoro), HttpStatus.OK);
	}

	@PostMapping("/pomodoros/past")
	public ResponseEntity<Pomodoro> createPastPomodoro(@Valid @RequestBody Pomodoro pomodoro, @RequestParam Long task_id,
			Principal principal) {
		Long user_id = Long.parseLong(principal.getName());

		// log.trace(pomodoro.toString() + task_id);
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
			throw new ResourceNotFoundException("task id:" + task_id);

		pomodoro.setUser(userEntry.get());
		pomodoro.setTask(taskEntry.get());
		pomodoro.setStatus("completed");
		log.trace("pomodoro for entry: {}", pomodoro);

		// TODO: get length from user settings stored in auth context
		UserSettings settings = userSettingsRepository.findUserSettings(user_id);
		pomodoro.setLength(settings.getPomodoroLength());

		Integer taskPomodoroLength = taskEntry.get().getPomodoroLength();
		if (taskPomodoroLength != 0) {
			pomodoro.setLength(taskPomodoroLength);
		} else {
			Integer projectPomodoroLength = taskEntry.get().getProject().getPomodoroLength();
			if (projectPomodoroLength != 0) {
				log.trace("setting length: " + projectPomodoroLength);
				pomodoro.setLength(projectPomodoroLength);
			}
		}
		
		if (pomodoro.getTimeElapsed() > pomodoro.getLength() * 60) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		log.trace("pomodoro length: {}", pomodoro.getLength());
		log.debug("pomodoro {}", pomodoro);
		
//		pomodoro.setTimeElapsed(pomodoro.getLength() * 60);

		return new ResponseEntity<>(pomodoroRepository.save(pomodoro), HttpStatus.OK);
	}

	@PutMapping("/pomodoros/{id}")
	public ResponseEntity<Pomodoro> updatePomodoro2(@PathVariable Long id,
			@RequestBody PomodoroUpdateDto pomodoroUpdateDto, Principal principal) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findById(id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);
		log.debug("new pomodoro request: {}, pomodoro entry: {}", pomodoroUpdateDto, pomodoroEntry.get());
		// TODO: log if timeElapsed calculated in front-end and back-end has large difference
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updateStatus(pomodoroUpdateDto.status());
		
		return new ResponseEntity<>(pomodoroRepository.save(runningPomodoro.getPomodoro()), HttpStatus.OK);
	}
	
	@GetMapping("/pomodoros/running")
	public ResponseEntity<PomodoroDto> getRunningPomodoro2(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<PomodoroDto> runningPomodoroEntry = pomodoroRepository.findRunningPomodoro(user_id);
		if (runningPomodoroEntry.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		log.debug("new get running pomodoro: {}", runningPomodoroEntry.get());
		
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id,
				runningPomodoroEntry.get().getId());
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + runningPomodoroEntry.get().getId());
		
		RunningPomodoro runningPomodoro = new RunningPomodoro(pomodoroEntry.get());
		runningPomodoro.updatePomodoroData();
		pomodoroRepository.save(runningPomodoro.getPomodoro());
		
		Optional<PomodoroDto> updatedRunningPomodoroEntry = pomodoroRepository.findRunningPomodoro(user_id);
		return new ResponseEntity<>(updatedRunningPomodoroEntry.get(), HttpStatus.OK);
	}
}

record PomodoroUpdateDto(@Min(value = 0) Integer timeElapsed, @NotBlank String status) {}
