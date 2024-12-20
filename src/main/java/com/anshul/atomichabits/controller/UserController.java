package com.anshul.atomichabits.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.UserService;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@EnableMethodSecurity
@RestController
public class UserController {

	private UserService userService;

	@GetMapping("/users")
	@PreAuthorize("hasAuthority('admin')")
	public List<User> retrieveAllUsers(Authentication authetication) {
		log.trace("authorities: " + authetication.getAuthorities());
		return userService.retriveAllUsers();
	}
}
