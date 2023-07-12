package com.anshul.atomichabits.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.AuthorityRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.User;

@RestController
public class Signup {

	private UserRepository userRepository;
	private AuthorityRepository authorityRepository;

	public Signup(UserRepository r, AuthorityRepository a) {
		this.userRepository = r;
		this.authorityRepository = a;
	}

	@PostMapping("/signup")
	public String saveUser(@RequestBody User user) {
		// System.out.println(user);

		userRepository.save(user);

		authorityRepository.save(new Authority(user, "user"));

		return "User created successfully";
	}
}
