package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return pomodoroRepository.findAllForToday(user.get().getId(), LocalDateTime.now().with(LocalTime.MIN));
	}

	@GetMapping("/pomodoros/projects-time")
	public List<Object> retrievePomodorosCountOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());
		List<Object> o = pomodoroRepository.findProjectsTime(user.get().getId());
		return o;
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
//			pomodoro.get().setEndTime(LocalDateTime.now(ZoneOffset.UTC));
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
			pomodoro.get().setEndTime(LocalDateTime.now(ZoneOffset.UTC));
		}
		
		System.out.println(pomodoro.get());
		pomodoro.get().setTimeElapsed(Integer.valueOf(timeElapsed));
		
		pomodoroRepository.save(pomodoro.get());
		
		return pomodoro.get();
	}
}
