package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.SlotRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Slot;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class SlotResource {

	private UserRepository userRepository;
	private SlotRepository slotRepository;
	

	public SlotResource(UserRepository u, SlotRepository s) {
		this.userRepository = u;
		this.slotRepository = s;
	}

	@GetMapping("/slots")
	public List<Slot> retrieveProjectsOfUser(Principal principal) {
		Optional<User> user = userRepository.findByUsername(principal.getName());

		return user.get().getSlots();
	}
	
	@PostMapping("/slots")
	public Slot retrieveProjectsOfUser(@Valid @RequestBody Slot slot, Principal principal) {
		System.out.println(slot);
		Optional<User> user = userRepository.findByUsername(principal.getName());
		
		slot.setUser(user.get());
		
		slotRepository.save(slot);
		
		return slot;
	}
}
