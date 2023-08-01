package com.anshul.atomichabits.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.anshul.atomichabits.dto.ProjectDto;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableMethodSecurity
@RestController
public class UserResource {

	private UserRepository userRepository;

	public UserResource(UserRepository r) {
		this.userRepository = r;
	}

	@GetMapping("/users")
	@PreAuthorize("hasAuthority('admin')")
	public List<User> retrieveAllUsers(Authentication authetication) {
		log.trace("authorities: " + authetication.getAuthorities());
		return userRepository.findAll();
	}

	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/users/change-password")
	public ResponseEntity<User> createProjectOfUser(@Valid @RequestBody PasswordDto passwordDto,
			Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		log.trace(passwordDto.password());
		
		Optional<User> userEntry = userRepository.findById(user_id);
		
		userEntry.get().setPassword(passwordDto.password());
		userRepository.save(userEntry.get());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

record PasswordDto(@NotBlank String password) {}
