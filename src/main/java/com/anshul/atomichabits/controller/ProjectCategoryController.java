package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.AccountabilityPartnerService;
import com.anshul.atomichabits.business.ProjectCategoryService;
import com.anshul.atomichabits.dto.ProjectCategoryDto;
import com.anshul.atomichabits.model.ProjectCategory;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class ProjectCategoryController {
	
	private ProjectCategoryService projectCategoryService;
	
	private AccountabilityPartnerService accountabilityPartnerService;
	
	@GetMapping("/project-categories/{id}")
	public ProjectCategory retrieveProjectCategory(Principal principal, @PathVariable Long id) {
		Long userId = Long.parseLong(principal.getName());

		return projectCategoryService.retriveProjectCategory(userId, id);
	}

	@GetMapping("/project-categories")
	public ResponseEntity<List<ProjectCategory>> retrieveProjectCategoriesOfUser(Principal principal,
			@RequestParam(required = false) Long subjectId,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {			
			return new ResponseEntity<>(projectCategoryService.retrieveAllProjectCategories(userId, limit, offset), HttpStatus.OK);
		} else {
			// TODO: move this check to filter or intercepter
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {				
				return new ResponseEntity<>(projectCategoryService.retrieveSubjectProjectCategories(subjectId, limit, offset), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@GetMapping("/project-categories/count")
	public Integer retrieveProjectCategoriesCountOfUser(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return projectCategoryService.retrieveAllProjectCategoriesCount(userId);
	}

	@PostMapping("/project-categories")
	public ResponseEntity<ProjectCategory> createProjectCategoryOfUser(Principal principal, 
			@Valid @RequestBody ProjectCategoryDto projectCategory) {
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(projectCategoryService.createProjectCategory(userId, projectCategory), HttpStatus.OK);
	}

	@PutMapping("/project-categories/{id}")
	public ResponseEntity<ProjectCategory> updateProjectCategoryOfUser(Principal principal, 
			@PathVariable Long id,
			@Valid @RequestBody ProjectCategoryDto projectCategory) {
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(projectCategoryService.updateProjectCategory(userId, id, projectCategory), HttpStatus.OK);
	}
}
