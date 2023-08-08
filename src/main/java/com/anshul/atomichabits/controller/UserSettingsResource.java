package com.anshul.atomichabits.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.jpa.*;
import com.anshul.atomichabits.model.*;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserSettingsResource {

	private UserSettingsRepository userSettingsRepository;

	@GetMapping("/user-settings")
	public UserSettings retrieveProject(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		UserSettings userSettings = userSettingsRepository.findUserSettings(user_id);
		
		return userSettings;
	}
}
