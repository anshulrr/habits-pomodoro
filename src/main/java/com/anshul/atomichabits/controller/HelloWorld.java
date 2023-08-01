package com.anshul.atomichabits.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloWorld {

	@GetMapping("/")
	public String getResponse(Authentication authentication) {
		log.debug("principal: " + authentication.getPrincipal());
		return "Hello " + authentication.getName();
	}
}
