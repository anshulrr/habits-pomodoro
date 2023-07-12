package com.anshul.atomichabits.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@Controller
public class UserController {

	private UserRepository userRepository;

	public UserController(UserRepository r) {
		userRepository = r;
	}

	@RequestMapping("/users-list")
	public String listAllUsers(ModelMap model) {
		List<User> users = userRepository.findAll();
		// System.out.println(users.get(1));
		// System.out.println(users.toString());
		// model.put("users", users.toString());
		model.put("users", users);
		return "listUsers";
	}
}
