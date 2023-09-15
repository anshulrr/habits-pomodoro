package com.anshul.atomichabits.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.anshul.atomichabits.business.UserSettingsService;
import com.anshul.atomichabits.dto.UserSettingsRequestDto;
import com.anshul.atomichabits.model.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserSettingsResource {

	@Autowired
	private UserSettingsService userSettingsService;

	@GetMapping("/user-settings")
	public UserSettings retrieveUserSettings(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		
		return userSettingsService.retriveUserSettings(user_id);
	}
	
	@PutMapping("/user-settings")
	public UserSettings updateUserSettings(Principal principal, @Valid @RequestBody UserSettingsRequestDto settingsRequest) {
		Long user_id = Long.parseLong(principal.getName());
		
		return userSettingsService.updateUserSettings(user_id, settingsRequest);
	}
}
