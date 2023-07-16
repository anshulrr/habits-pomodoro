package com.anshul.atomichabits.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.AuthorityRepository;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

@RestController
public class Signup {

	private UserRepository userRepository;
	private AuthorityRepository authorityRepository;
	private ProjectCategoryRepository projectCategoryRepository;

	public Signup(UserRepository r, AuthorityRepository a, ProjectCategoryRepository p) {
		this.userRepository = r;
		this.authorityRepository = a;
		this.projectCategoryRepository = p;
	}

	@PostMapping("/signup")
	public String saveUser(@RequestBody User user) {
		userRepository.save(user);

		authorityRepository.save(new Authority(user, "user"));
		
		// Create few initial project categories for reference
		createInitialProjectCategories(user);

		return "User created successfully";
	}

	private void createInitialProjectCategories(User user) {
		// create two project category named General, Hobbies
		ProjectCategory projectCategory1 = new ProjectCategory();
		projectCategory1.setUser(user);
		projectCategory1.setName("General");
		projectCategory1.setLevel(1);
		projectCategoryRepository.save(projectCategory1);

		ProjectCategory projectCategory2 = new ProjectCategory();
		projectCategory2.setUser(user);
		projectCategory2.setName("Hobbies");
		projectCategory2.setLevel(10);
		projectCategoryRepository.save(projectCategory2);

		ProjectCategory projectCategory3 = new ProjectCategory();
		projectCategory3.setUser(user);
		projectCategory3.setName("Rest");
		projectCategory3.setLevel(20);
		projectCategoryRepository.save(projectCategory3);
	}
}
