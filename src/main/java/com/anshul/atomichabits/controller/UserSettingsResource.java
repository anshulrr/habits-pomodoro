package com.anshul.atomichabits.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.*;

import com.anshul.atomichabits.business.UserSettingsService;
import com.anshul.atomichabits.dto.UserSettingsRequestDto;
import com.anshul.atomichabits.model.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserSettingsResource {

	private UserSettingsService userSettingsService;

	@GetMapping("/user-settings")
	public UserSettings retrieveUserSettings(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		
		return userSettingsService.retriveUserSettings(userId);
	}
	
	@PutMapping("/user-settings")
	public UserSettings updateUserSettings(Principal principal, @Valid @RequestBody UserSettingsRequestDto settingsRequest) {
		Long userId = Long.parseLong(principal.getName());
		
		return userSettingsService.updateUserSettings(userId, settingsRequest);
	}
}
