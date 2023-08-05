package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
	public List<PomodoroForList> retrievePomodorosOfUser(Principal principal, @RequestParam(defaultValue = "0") int offset, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		OffsetDateTime date = OffsetDateTime.now().with(LocalTime.MIN);
		date = date.plusDays(offset);
		OffsetDateTime end = date.plusDays(1);
		log.trace(date + " " + end);
		List<PomodoroForList> pomodoros = pomodoroRepository.findAllForToday(user_id, date, end, categories);
		log.trace("first pomodoro: {}", pomodoros.size() != 0 ?  pomodoros.get(0).getId() : "nill");

		return pomodoros;
	}

	@GetMapping("/stats/projects-time")
	public List<Object> retrieveProjectPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		List<Object> result;

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.with(LocalTime.MIN);
			monday = monday.plusDays(7 * offset);
			OffsetDateTime end = monday.plusDays(7);
			log.trace(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findProjectsTime(user_id, monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(offset);
			OffsetDateTime end = first.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			log.trace(first + " " + end);
			result = pomodoroRepository.findProjectsTime(user_id, first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().with(LocalTime.MIN);
			date = date.plusDays(offset);
			OffsetDateTime end = date.plusDays(1);
			log.trace(date + " " + end);
			result = pomodoroRepository.findProjectsTime(user_id, date, end, categories);
		}

		return result;
	}

	@GetMapping("/stats/tasks-time")
	public List<Object> retrieveTaskPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		List<Object> result;

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.with(LocalTime.MIN);
			monday = monday.plusDays(7 * offset);
			OffsetDateTime end = monday.plusDays(7);
			log.trace(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findTasksTime(user_id, monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(offset);
			OffsetDateTime end = first.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			log.trace(first + " " + end);
			result = pomodoroRepository.findTasksTime(user_id, first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().with(LocalTime.MIN);
			date = date.plusDays(offset);
			OffsetDateTime end = date.plusDays(1);
			log.trace(date + " " + end);
			result = pomodoroRepository.findTasksTime(user_id, date, end, categories);
		}

		return result;
	}

	@GetMapping("/stats/total-time")
	public Map<String, List<String[]>> retrieveTotalPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		List<String[]> result;
		log.trace(limit + " " + offset);

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.plusDays(-14 * 7).with(LocalTime.MIN);
			monday = monday.plusDays(15 * 7 * offset);
			OffsetDateTime end = monday.plusDays(14 * 7).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
					.with(LocalTime.MAX);
			log.trace(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findTotalTimeWeekly(user_id, monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().plusMonths(-14).withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(15 * offset);
			OffsetDateTime end = first.plusMonths(14).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			log.trace(first + " " + end);
			result = pomodoroRepository.findTotalTimeMonthly(user_id, first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().plusDays(-14).with(LocalTime.MIN);
			date = date.plusDays(15 * offset);
			OffsetDateTime end = date.plusDays(14).with(LocalTime.MAX);
			log.trace(date + " " + end);
			result = pomodoroRepository.findTotalTimeDaily(user_id, date, end, categories);
		}
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
