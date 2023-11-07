package com.anshul.atomichabits.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.business.AccountabilityPartnerService;

import jakarta.validation.Valid;

@RestController
public class AccountabilityPartnerResource {

	@Autowired
	private AccountabilityPartnerService accountabilityPartnerService;
	
	@GetMapping("/accountability-partners")
	public List<UserForList> getPartners(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return  accountabilityPartnerService.getPartners(user_id);
	}

	@GetMapping("/accountability-subjects")
	public List<UserForList> getSubjects(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return  accountabilityPartnerService.getSubjects(user_id);
	}

	@PostMapping("/accountability-partners")
	public ResponseEntity<Boolean> createPartner(Principal principal, @Valid @RequestBody AccountabilityPartnerRequest request) {
		Long user_id = Long.parseLong(principal.getName());
		try {
			accountabilityPartnerService.addPartner(user_id, request.email());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/accountability-partners/{id}")
	public ResponseEntity<Boolean> deletePartner(Principal principal, @PathVariable Long id) {
		Long user_id = Long.parseLong(principal.getName());
		System.out.println(user_id + " " + id);
		accountabilityPartnerService.deletePartner(user_id, id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

record AccountabilityPartnerRequest(String email) {};
