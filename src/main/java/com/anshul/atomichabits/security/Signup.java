package com.anshul.atomichabits.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@RestController
public class Signup {
	
	private  UserRepository userRepository;
	
	public Signup(UserRepository r) {
		this.userRepository = r;
	}
	
	@PostMapping("/signup")
	public String saveUser(@RequestBody User user) {
		// System.out.println(user);
		
		userRepository.save(user);
		
		return "User created successfully";
	}
}
