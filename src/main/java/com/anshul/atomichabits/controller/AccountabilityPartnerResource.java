package com.anshul.atomichabits.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.business.AccountabilityPartnerService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class AccountabilityPartnerResource {

	private AccountabilityPartnerService accountabilityPartnerService;
	
	@GetMapping("/accountability-partners")
	public List<UserForList> getPartners(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return  accountabilityPartnerService.getPartners(userId);
	}

	@GetMapping("/accountability-subjects")
	public List<UserForList> getSubjects(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return  accountabilityPartnerService.getSubjects(userId);
	}

	@PostMapping("/accountability-partners")
	public ResponseEntity<Boolean> createPartner(Principal principal, @Valid @RequestBody AccountabilityPartnerRequest request) {
		Long userId = Long.parseLong(principal.getName());
		try {
			accountabilityPartnerService.addPartner(userId, request.email());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/accountability-partners/{id}")
	public ResponseEntity<Boolean> deletePartner(Principal principal, @PathVariable Long id) {
		Long userId = Long.parseLong(principal.getName());
		accountabilityPartnerService.deletePartner(userId, id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

record AccountabilityPartnerRequest(String email) {}
