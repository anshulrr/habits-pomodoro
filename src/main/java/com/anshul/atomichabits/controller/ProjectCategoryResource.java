package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProjectCategoryResource {

	private UserRepository userRepository;
	private ProjectCategoryRepository projectCategoryRepository;

	public ProjectCategoryResource(UserRepository u, ProjectCategoryRepository p) {
		this.userRepository = u;
		this.projectCategoryRepository = p;
	}

	@GetMapping("/project-categories/{id}")
	public ProjectCategory retrieveProject(@PathVariable Long id, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);

		return categoryEntry.get();
	}

	@GetMapping("/project-categories")
	public List<ProjectCategory> retrieveProjectsOfUser(Principal principal,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryRepository.findUserProjectCategories(user_id, limit, offset);
	}

	@GetMapping("/project-categories/count")
	public Integer retrieveProjectsCountOfUser(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryRepository.getUserProjectCategoriesCount(user_id);
	}

	@PostMapping("/project-categories")
	public ResponseEntity<ProjectCategory> createProjectOfUser(@Valid @RequestBody ProjectCategory projectCategory,
			Principal principal) {
		try {
			Long user_id = Long.parseLong(principal.getName());
			Optional<User> userEntry = userRepository.findById(user_id);

			projectCategory.setUser(userEntry.get());
			return new ResponseEntity<>(projectCategoryRepository.save(projectCategory), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			log.debug("" + e);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/project-categories/{id}")
	public ResponseEntity<ProjectCategory> createProjectOfUser(@PathVariable Long id,
			@Valid @RequestBody ProjectCategory projectCategory, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, id);
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + id);

		try {
			categoryEntry.get().setName(projectCategory.getName());
			categoryEntry.get().setLevel(projectCategory.getLevel());
			categoryEntry.get().setStatsDefault(projectCategory.isStatsDefault());
			return new ResponseEntity<>(projectCategoryRepository.save(categoryEntry.get()), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			log.debug("" + e);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
