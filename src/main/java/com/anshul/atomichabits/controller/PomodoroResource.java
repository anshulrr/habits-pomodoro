package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.*;
import java.util.*;
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

import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PomodoroResource {

	private UserRepository userRepository;
	private PomodoroRepository pomodoroRepository;
	private TaskRepository taskRepository;

	public PomodoroResource(UserRepository u, PomodoroRepository p, TaskRepository t) {
		this.userRepository = u;
		this.pomodoroRepository = p;
		this.taskRepository = t;
	}

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

		// TODO: get length from user settings
		pomodoro.setLength(25);

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

	//	getting request data as params
	@PutMapping(value = "/pomodoros/{id}", params = { "timeElapsed", "status" })
	public ResponseEntity<Pomodoro> updatePomodoro(@PathVariable Long id,
			@RequestParam("timeElapsed") String timeElapsed, @RequestParam("status") String status,
			Principal principal) {
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findById(id);
		if (pomodoroEntry.isEmpty())
			throw new ResourceNotFoundException("pomodoro id:" + id);

		// Extra check for sync pomodoro
		if (pomodoroEntry.get().getStatus().equals("completed")) {
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}

		log.trace(status);
		pomodoroEntry.get().setStatus(status);

		if (status.equals("completed")) {
			pomodoroEntry.get().setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
		}

		// Update the startTime, so that refresh api and sync logic works correctly
		if (status.equals("started")) {
			OffsetDateTime updatedStartTime = OffsetDateTime.now(ZoneOffset.UTC)
					.minusSeconds(pomodoroEntry.get().getTimeElapsed());
			log.trace(updatedStartTime + " : " + pomodoroEntry.get().getTimeElapsed());
			pomodoroEntry.get().setStartTime(updatedStartTime);
		}

		log.trace("" + pomodoroEntry.get());
		pomodoroEntry.get().setTimeElapsed(Integer.valueOf(timeElapsed));

		return new ResponseEntity<>(pomodoroRepository.save(pomodoroEntry.get()), HttpStatus.OK);
	}

	@GetMapping("/pomodoros/running")
	public ResponseEntity<PomodoroDto> getRunningPomodoro(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<PomodoroDto> runningPomodoroEntry = pomodoroRepository.findRunningPomodoro(user_id);
		if (runningPomodoroEntry.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}

		// Automatically update timeElapsed for pomodoro with status started
		// Otherwise it will start again without considering actual time elapsed
		if (runningPomodoroEntry.get().getStatus().equals("started")) {
			Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id,
					runningPomodoroEntry.get().getId());
			if (pomodoroEntry.isEmpty())
				throw new ResourceNotFoundException("pomodoro id:" + runningPomodoroEntry.get().getId());

			OffsetDateTime startTime = pomodoroEntry.get().getStartTime();

			Long timeElapsed = Duration.between(startTime, OffsetDateTime.now()).getSeconds();
			log.trace("" + Duration.between(startTime, OffsetDateTime.now()).getSeconds());
			if (timeElapsed < pomodoroEntry.get().getLength() * 60) {
				pomodoroEntry.get().setTimeElapsed(Math.toIntExact(timeElapsed));
			} else {
				pomodoroEntry.get().setTimeElapsed(pomodoroEntry.get().getLength() * 60 - 3);
			}
			pomodoroRepository.save(pomodoroEntry.get());

			Optional<PomodoroDto> updatedRunningPomodoroEntry = pomodoroRepository.findRunningPomodoro(user_id);
			return new ResponseEntity<>(updatedRunningPomodoroEntry.get(), HttpStatus.OK);
		}

		return new ResponseEntity<>(runningPomodoroEntry.get(), HttpStatus.OK);
	}
}
