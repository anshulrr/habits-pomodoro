package com.anshul.atomichabits.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {

	@GetMapping("/")
	public String getResponse(Authentication authentication) {
		// System.out.println(authentication.getPrincipal());
		return "Hello " + authentication.getName();
	}
}
