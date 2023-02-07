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
import com.anshul.atomichabits.jpa.SlotRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Slot;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class SlotResource {

	private UserRepository userRepository;
	private SlotRepository slotRepository;
	private TaskRepository taskRepository;
	

	public SlotResource(UserRepository u, SlotRepository s, TaskRepository t) {
		this.userRepository = u;
		this.slotRepository = s;
		this.taskRepository = t;
	}

	@GetMapping("/slots")
	public List<Slot> retrieveProjectsOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		return user.get().getSlots();
	}
	
	@PostMapping("/slots")
	public Slot retrieveProjectsOfUser(@Valid @RequestBody Slot slot, @RequestParam Long task_id, Principal principal) {
		System.out.println(slot.toString() + task_id);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		Optional<Task> task = taskRepository.findUserTaskById(user.get(), task_id);
		
		slot.setUser(user.get());
		slot.setTask(task.get());
		
		System.out.println(slot);
		
		slotRepository.save(slot);
		
		return slot;
	}
}
