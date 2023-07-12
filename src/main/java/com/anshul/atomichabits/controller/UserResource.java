package com.anshul.atomichabits.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;

@RestController
public class UserResource {

	private UserRepository userRepository;

	public UserResource(UserRepository r) {
		this.userRepository = r;
	}

	@GetMapping("/users")
	public List<User> retrieveAllUsers() {
		return userRepository.findAll();
	}

	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
}
