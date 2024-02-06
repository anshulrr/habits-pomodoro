package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.TagService;
import com.anshul.atomichabits.model.Tag;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class TagResource {

	private TagService tagService;

	@GetMapping("/tags/{id}")
	public Tag retrieveTag(Principal principal, @PathVariable Long id) {
		Long userId = Long.parseLong(principal.getName());
		return tagService.retriveTag(userId, id);
	}

	@GetMapping("/tags")
	public List<Tag> retrieveTagsOfUser(Principal principal,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		return tagService.retrieveAllTags(userId, limit, offset);
	}

	@GetMapping("/tags/count")
	public Integer retrieveTagsCountOfUser(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return tagService.retrieveAllTagsCount(userId);
	}

	@PostMapping("/tags")
	public Tag createTagOfUser(Principal principal, 
			@Valid @RequestBody Tag tagRequest) {
		Long userId = Long.parseLong(principal.getName());
		return tagService.createTag(userId, tagRequest);
	}

	@PutMapping("/tags/{id}")
	public Tag updateTagOfUser(Principal principal, 
			@PathVariable Long id,
			@Valid @RequestBody Tag tagRequest) {
		Long userId = Long.parseLong(principal.getName());
		return tagService.updateTag(userId, id, tagRequest);
	}
}
