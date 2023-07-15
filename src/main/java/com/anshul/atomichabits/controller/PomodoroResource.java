package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
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
	public List<PomodoroForList> retrievePomodorosOfUser(Principal principal, @RequestParam("include_categories") long[] categories) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		List<PomodoroForList> pomodoros = pomodoroRepository.findAllForToday(user.get().getId(),
				OffsetDateTime.now().with(LocalTime.MIN), categories);

		// System.out.println(pomodoros.get(0).getId());

		return pomodoros;
	}

	@GetMapping("/stats/projects-time")
	public List<Object> retrieveProjectPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<Object> result;

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.with(LocalTime.MIN);
			monday = monday.plusDays(7 * offset);
			OffsetDateTime end = monday.plusDays(7);
			// System.out.println(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findProjectsTime(user.get().getId(), monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(offset);
			OffsetDateTime end = first.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			// System.out.println(first + " " + end);
			result = pomodoroRepository.findProjectsTime(user.get().getId(), first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().with(LocalTime.MIN);
			date = date.plusDays(offset);
			OffsetDateTime end = date.plusDays(1);
			// System.out.println(date + " " + end);
			result = pomodoroRepository.findProjectsTime(user.get().getId(), date, end, categories);
		}

		return result;
	}

	@GetMapping("/stats/tasks-time")
	public List<Object> retrieveTaskPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		List<Object> result;

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.with(LocalTime.MIN);
			monday = monday.plusDays(7 * offset);
			OffsetDateTime end = monday.plusDays(7);
			// System.out.println(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findTasksTime(user.get().getId(), monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(offset);
			OffsetDateTime end = first.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			// System.out.println(first + " " + end);
			result = pomodoroRepository.findTasksTime(user.get().getId(), first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().with(LocalTime.MIN);
			date = date.plusDays(offset);
			OffsetDateTime end = date.plusDays(1);
			// System.out.println(date + " " + end);
			result = pomodoroRepository.findTasksTime(user.get().getId(), date, end, categories);
		}

		return result;
	}

	@GetMapping("/stats/total-time")
	public Map<String, List<String[]>> retrieveTotalPomodoros(Principal principal, @RequestParam("limit") String limit,
			@RequestParam("offset") int offset, @RequestParam("include_categories") long[] categories) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<String[]> result;

		// System.out.println(limit + " " + offset);

		if (limit.equals("weekly")) {
			OffsetDateTime monday = OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
					.plusDays(-14 * 7).with(LocalTime.MIN);
			monday = monday.plusDays(15 * 7 * offset);
			OffsetDateTime end = monday.plusDays(14 * 7).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
					.with(LocalTime.MAX);
			// System.out.println(OffsetDateTime.now().getDayOfWeek() + " " + monday + " " + end);
			result = pomodoroRepository.findTotalTimeWeekly(user.get().getId(), monday, end, categories);
		} else if (limit.equals("monthly")) {
			OffsetDateTime first = OffsetDateTime.now().plusMonths(-14).withDayOfMonth(1).with(LocalTime.MIN);
			first = first.plusMonths(15 * offset);
			OffsetDateTime end = first.plusMonths(14).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			// System.out.println(first + " " + end);
			result = pomodoroRepository.findTotalTimeMonthly(user.get().getId(), first, end, categories);
		} else {
			OffsetDateTime date = OffsetDateTime.now().plusDays(-14).with(LocalTime.MIN);
			date = date.plusDays(15 * offset);
			OffsetDateTime end = date.plusDays(14).with(LocalTime.MAX);
			// System.out.println(date + " " + end);
			result = pomodoroRepository.findTotalTimeDaily(user.get().getId(), date, end, categories);
		}

		Map<String, List<String[]>> groupedResult = result.stream()
				.collect(Collectors.groupingBy((element -> (String) element[2])));
		// System.out.println(result);

		return groupedResult;
	}

	@PostMapping("/pomodoros")
	public ResponseEntity<Pomodoro> createPomodoro(@Valid @RequestBody Pomodoro pomodoro, @RequestParam Long task_id,
			Principal principal) {
		// System.out.println(pomodoro.toString() + task_id);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Task> task = taskRepository.findUserTaskById(user.get(), task_id);

		pomodoro.setUser(user.get());
		pomodoro.setTask(task.get());

		Optional<PomodoroDto> runningPomodoro = pomodoroRepository.findRunningPomodoro(user.get());

		if (runningPomodoro.isPresent()) {
			System.out.println(runningPomodoro);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// System.out.println(pomodoro);
		// System.out.println(task.get().getProject().getPomodoroLength());

		// TODO: get length from user settings
		pomodoro.setLength(25);

		Integer taskPomodoroLength = task.get().getPomodoroLength();
		if (taskPomodoroLength != 0) {
			pomodoro.setLength(taskPomodoroLength);
		} else {
			Integer projectPomodoroLength = task.get().getProject().getPomodoroLength();
			if (projectPomodoroLength != 0) {
				// 			System.out.println("setting length: " + projectPomodoroLength);
				pomodoro.setLength(projectPomodoroLength);
			}
		}
		// System.out.println(pomodoro.getLength());

		return new ResponseEntity<>(pomodoroRepository.save(pomodoro), HttpStatus.OK);
	}

	//	@PutMapping("/pomodoros/{id}")
	//	public Pomodoro updatePomodor(@PathVariable Long id, @Valid @RequestBody Map<String, String> json, Principal principal) {
	// Optional<User> user = userRepository.findByUsername(principal.getName());
	// Optional<Pomodoro> pomodoro = pomodoroRepository.findById(id);
	// 
	// System.out.println(json);
	// pomodoro.get().setStatus(json.get("status"));
	// 
	// if (json.get("status").equals("completed")) {
	// 	pomodoro.get().setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
	// }
	// 
	// System.out.println(pomodoro.get());
	// pomodoro.get().setTimeElapsed(Integer.valueOf(json.get("timeElapsed")));
	// 
	// pomodoroRepository.save(pomodoro.get());
	// 
	// return pomodoro.get();
	//	}

	//	getting request data as params
	@PutMapping(value = "/pomodoros/{id}", params = { "timeElapsed", "status" })
	public ResponseEntity<Pomodoro> updatePomodoro(@PathVariable Long id,
			@RequestParam("timeElapsed") String timeElapsed, @RequestParam("status") String status,
			Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Pomodoro> pomodoro = pomodoroRepository.findById(id);

		// Extra check for sync pomodoro
		if (pomodoro.get().getStatus().equals("completed")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// System.out.println(status);
		pomodoro.get().setStatus(status);

		if (status.equals("completed")) {
			pomodoro.get().setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
		}

		// Update the startTime, so that refresh api and sync logic works correctly
		if (status.equals("started")) {
			OffsetDateTime updatedStartTime = OffsetDateTime.now(ZoneOffset.UTC)
					.minusSeconds(pomodoro.get().getTimeElapsed());
			// System.out.println(updatedStartTime + " : " + pomodoro.get().getTimeElapsed());
			pomodoro.get().setStartTime(updatedStartTime);
		}

		// System.out.println(pomodoro.get());
		pomodoro.get().setTimeElapsed(Integer.valueOf(timeElapsed));

		return new ResponseEntity<>(pomodoroRepository.save(pomodoro.get()), HttpStatus.OK);
	}

	@GetMapping("/pomodoros/running")
	public ResponseEntity<PomodoroDto> getRunningPomodoro(Principal principal) {
		// System.out.println(pomodoro.toString() + task_id);
		Optional<User> user = userRepository.findByUsername(principal.getName());

		Optional<PomodoroDto> runningPomodoro = pomodoroRepository.findRunningPomodoro(user.get());

		if (runningPomodoro.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// Automatically update timeElapsed for pomodoro with status started
		// Otherwise it will start again without considering actual time elapsed
		if (runningPomodoro.get().getStatus().equals("started")) {
			Optional<Pomodoro> pomodoro = pomodoroRepository.findUserPomodoroById(user.get(),
					runningPomodoro.get().getId());
			OffsetDateTime startTime = pomodoro.get().getStartTime();

			Long timeElapsed = Duration.between(startTime, OffsetDateTime.now()).getSeconds();
			// System.out.println(Duration.between(startTime, OffsetDateTime.now()).getSeconds());
			if (timeElapsed < pomodoro.get().getLength() * 60) {
				pomodoro.get().setTimeElapsed(Math.toIntExact(timeElapsed));
			} else {
				pomodoro.get().setTimeElapsed(pomodoro.get().getLength() * 60 - 3);
			}
			pomodoroRepository.save(pomodoro.get());

			Optional<PomodoroDto> updatedRunningPomodoro = pomodoroRepository.findRunningPomodoro(user.get());

			return new ResponseEntity<>(updatedRunningPomodoro.get(), HttpStatus.OK);
		}

		return new ResponseEntity<>(runningPomodoro.get(), HttpStatus.OK);
	}
}
