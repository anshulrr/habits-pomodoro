package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
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
	public List<Pomodoro> retrievePomodorosOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		return pomodoroRepository.findAllForToday(user.get().getId(), OffsetDateTime.now().with(LocalTime.MIN));
	}

	@GetMapping("/pomodoros/projects-time")
	public List<Object> retrieveProjectPomodoros(Principal principal, @RequestParam("limit") String limit) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<Object> o;
		
		if (limit.equals("weekly")) {
			// todo: fix first day of week
			System.out.println(OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
			o = pomodoroRepository.findProjectsTime(user.get().getId(), OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
		} else if (limit.equals("monthly")) {
			System.out.println(OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
			o = pomodoroRepository.findProjectsTime(user.get().getId(), OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
		} else {
			o = pomodoroRepository.findProjectsTime(user.get().getId(), OffsetDateTime.now().with(LocalTime.MIN));
		}
		
		return o;
	}

	@GetMapping("/pomodoros/tasks-time")
	public List<Object> retrieveTaskPomodoros(Principal principal, @RequestParam("limit") String limit) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<Object> o;
		
		if (limit.equals("weekly")) {
			// todo: fix first day of week
			System.out.println(OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
			o = pomodoroRepository.findTasksTime(user.get().getId(), OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
		} else if (limit.equals("monthly")) {
			System.out.println(OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
			o = pomodoroRepository.findTasksTime(user.get().getId(), OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
		} else {
			o = pomodoroRepository.findTasksTime(user.get().getId(), OffsetDateTime.now().with(LocalTime.MIN));
		}
		
		return o;
	}
	

	@GetMapping("/pomodoros/total-time")
	public Map<String, List<String[]>> retrieveTotalPomodoros(Principal principal, @RequestParam("limit") String limit) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<String[]> o;
		
		if (limit.equals("weekly")) {
			// todo: fix first day of week
			System.out.println(OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
			o = pomodoroRepository.findTotalTime(user.get().getId(), OffsetDateTime.now().minusWeeks(1).with(LocalTime.MIN));
		} else if (limit.equals("monthly")) {
			System.out.println(OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
			o = pomodoroRepository.findTotalTime(user.get().getId(), OffsetDateTime.now().withDayOfMonth(1).with(LocalTime.MIN));
		} else {
			o = pomodoroRepository.findTotalTime(user.get().getId(), OffsetDateTime.now().with(LocalTime.MIN));
		}
		
//		List<Pomodoro> temp = pomodoroRepository.findAllForToday(user.get().getId(), OffsetDateTime.now().with(LocalTime.MIN));
		
//		Map<OffsetDateTime, List<Pomodoro>> result = temp.stream()
//				  .collect(Collectors.groupingBy(Pomodoro::getStartTime));
		
		Map<String, List<String[]>> result = o.stream()
				  .collect(Collectors.groupingBy((element -> (String)element[2])));
		
		
		System.out.println(result);
		
		return result;
	}
	
	@PostMapping("/pomodoros")
	public Pomodoro createPomodoro(@Valid @RequestBody Pomodoro pomodoro, @RequestParam Long task_id, Principal principal) {
		System.out.println(pomodoro.toString() + task_id);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Task> task = taskRepository.findUserTaskById(user.get(), task_id);
		
		pomodoro.setUser(user.get());
		pomodoro.setTask(task.get());
		
		System.out.println(pomodoro);
		
		pomodoroRepository.save(pomodoro);
		
		return pomodoro;
	}
	
//	@PutMapping("/pomodoros/{id}")
//	public Pomodoro updatePomodor(@PathVariable Long id, @Valid @RequestBody Map<String, String> json, Principal principal) {
//		Optional<User> user = userRepository.findByUsername(principal.getName());
//		Optional<Pomodoro> pomodoro = pomodoroRepository.findById(id);
//		
//		System.out.println(json);
//		pomodoro.get().setStatus(json.get("status"));
//		
//		if (json.get("status").equals("completed")) {
//			pomodoro.get().setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
//		}
//		
//		System.out.println(pomodoro.get());
//		pomodoro.get().setTimeElapsed(Integer.valueOf(json.get("timeElapsed")));
//		
//		pomodoroRepository.save(pomodoro.get());
//		
//		return pomodoro.get();
//	}
	

//	getting request data as params
	@PutMapping(value = "/pomodoros/{id}", 
			  params = { "timeElapsed", "status" })
	public Pomodoro updatePomodor(@PathVariable Long id, @RequestParam("timeElapsed") String timeElapsed, @RequestParam("status") String status,  Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Pomodoro> pomodoro = pomodoroRepository.findById(id);
		
		System.out.println(status);
		pomodoro.get().setStatus(status);
		
		if (status.equals("completed")) {
			pomodoro.get().setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
		}
		
		System.out.println(pomodoro.get());
		pomodoro.get().setTimeElapsed(Integer.valueOf(timeElapsed));
		
		pomodoroRepository.save(pomodoro.get());
		
		return pomodoro.get();
	}
}
