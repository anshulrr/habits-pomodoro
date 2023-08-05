package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.UserService;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableMethodSecurity
@RestController
public class UserResource {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	@PreAuthorize("hasAuthority('admin')")
	public List<User> retrieveAllUsers(Authentication authetication) {
		log.trace("authorities: " + authetication.getAuthorities());
		return userService.retriveAllUsers();
	}

	@PutMapping("/users/change-password")
	public ResponseEntity<User> createProjectOfUser(@Valid @RequestBody PasswordDto passwordDto, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		log.trace(passwordDto.password());

		userService.updatePassword(user_id, passwordDto.password());

		return new ResponseEntity<>(HttpStatus.OK);
	}
}

record PasswordDto(@NotBlank String password) {}
