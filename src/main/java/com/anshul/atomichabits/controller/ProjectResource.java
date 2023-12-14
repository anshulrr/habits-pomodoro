package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.AccountabilityPartnerService;
import com.anshul.atomichabits.business.ProjectService;
import com.anshul.atomichabits.dto.ProjectDto;
import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.model.Project;

import jakarta.validation.Valid;

@RestController
public class ProjectResource {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private AccountabilityPartnerService accountabilityPartnerService;

	@GetMapping("/projects/{id}")
	public ProjectDto retrieveProject(Principal principal, @PathVariable Long id) {
		Long user_id = Long.parseLong(principal.getName());
		return projectService.retriveProject(user_id, id);
	}

	@GetMapping("/projects")
	public ResponseEntity<List<ProjectForList>> retrieveProjectsOfUser(Principal principal,
			@RequestParam(required = false) Long subjectId,
			@RequestParam(defaultValue = "current") String status,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(required = false) Long categoryId) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId != null) {
			if (accountabilityPartnerService.isSubject(user_id, subjectId)) {
				user_id = subjectId;
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		if (categoryId == null) {			
			return new ResponseEntity<>(projectService.retrieveAllProjects(user_id, status, limit, offset), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(projectService.retrieveCategoryProjects(user_id, categoryId), HttpStatus.OK);
		}
	}

	@GetMapping("/projects/count")
	public Integer retrieveProjectsCountOfUser(Principal principal, @RequestParam(defaultValue = "current") String status) {
		Long user_id = Long.parseLong(principal.getName());
		return projectService.retrieveProjectsCount(user_id, status);
	}

	@PostMapping("/projects")
	public ProjectDto createProjectOfUser(Principal principal, 
			@Valid @RequestBody ProjectDto projectDto) {
		Long user_id = Long.parseLong(principal.getName());
		return projectService.createProject(user_id, projectDto);
	}

	@PutMapping("/projects/{id}")
	public Project updateProjectOfUser(Principal principal, 
			@PathVariable Long id,
			@Valid @RequestBody ProjectDto projectDto) {
		Long user_id = Long.parseLong(principal.getName());
		return projectService.updateProject(user_id, id, projectDto);
	}
}
