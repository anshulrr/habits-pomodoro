package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	public List<Pomodoro> retrieveProjectsOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		return user.get().getPomodoros();
	}
	
	@PostMapping("/pomodoros")
	public Pomodoro retrieveProjectsOfUser(@Valid @RequestBody Pomodoro pomodoro, @RequestParam Long task_id, Principal principal) {
		System.out.println(pomodoro.toString() + task_id);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Task> task = taskRepository.findUserTaskById(user.get(), task_id);
		
		pomodoro.setUser(user.get());
		pomodoro.setTask(task.get());
		
		System.out.println(pomodoro);
		
		pomodoroRepository.save(pomodoro);
		
		return pomodoro;
	}
}
