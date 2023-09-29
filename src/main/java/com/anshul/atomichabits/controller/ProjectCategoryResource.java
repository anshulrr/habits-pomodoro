package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.ProjectCategoryService;
import com.anshul.atomichabits.model.ProjectCategory;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProjectCategoryResource {
	
	@Autowired
	private ProjectCategoryService projectCategoryService;
	
	@GetMapping("/project-categories/{id}")
	public ProjectCategory retrieveProjectCategory(Principal principal, @PathVariable Long id) {
		Long user_id = Long.parseLong(principal.getName());

		return projectCategoryService.retriveProjectCategory(user_id, id);
	}

	@GetMapping("/project-categories")
	public List<ProjectCategory> retrieveProjectCategoriesOfUser(Principal principal,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryService.retrieveAllProjectCategories(user_id, limit, offset);
	}

	@GetMapping("/project-categories/count")
	public Integer retrieveProjectCategoriesCountOfUser(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return projectCategoryService.retrieveAllProjectCategoriesCount(user_id);
	}

	@PostMapping("/project-categories")
	public ResponseEntity<ProjectCategory> createProjectCategoryOfUser(Principal principal, 
			@Valid @RequestBody ProjectCategory projectCategory) {
		Long user_id = Long.parseLong(principal.getName());
		try {
			return new ResponseEntity<>(projectCategoryService.createProjectCategory(user_id, projectCategory), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			log.debug("" + e);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/project-categories/{id}")
	public ResponseEntity<ProjectCategory> updateProjectCategoryOfUser(Principal principal, 
			@PathVariable Long id,
			@Valid @RequestBody ProjectCategory projectCategory) {
		Long user_id = Long.parseLong(principal.getName());
		try {
			return new ResponseEntity<>(projectCategoryService.updateProjectCategory(user_id, id, projectCategory), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			log.debug("" + e);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
