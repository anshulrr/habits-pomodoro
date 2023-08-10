package com.anshul.atomichabits.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.*;

import com.anshul.atomichabits.jpa.*;
import com.anshul.atomichabits.model.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class UserSettingsResource {

	private UserSettingsRepository userSettingsRepository;

	@GetMapping("/user-settings")
	public UserSettings retrieveProject(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		UserSettings userSettings = userSettingsRepository.findUserSettings(user_id);
		log.debug("settings: {}", userSettings);
		
		return userSettings;
	}
	
	@PutMapping("/user-settings")
	public UserSettings updateProjectOfUser(@Valid @RequestBody UserSettings settingsRequest, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		
		// todo: request validation
		UserSettings userSettings = userSettingsRepository.findUserSettings(user_id);
		
		userSettings.setPomodoroLength(settingsRequest.getPomodoroLength());
		userSettings.setBreakLength(settingsRequest.getBreakLength());
		
		userSettings.setEnableStopwatch(settingsRequest.isEnableStopwatch());
		userSettings.setEnableStopwatchAudio(settingsRequest.isEnableStopwatchAudio());
		
		userSettings.setEnableChartScale(settingsRequest.isEnableChartScale());
		userSettings.setChartScale(settingsRequest.getChartScale());
		
		userSettings.setEnableChartWeeklyAverage(settingsRequest.isEnableChartWeeklyAverage());
		userSettings.setChartWeeklyAverage(settingsRequest.getChartWeeklyAverage());
		
		userSettings.setEnableChartMonthlyAverage(settingsRequest.isEnableChartMonthlyAverage());
		userSettings.setChartMonthlyAverage(settingsRequest.getChartMonthlyAverage());
		
		userSettings.setEnableChartAdjustedWeeklyMonthlyAverage(settingsRequest.isEnableChartAdjustedWeeklyMonthlyAverage());
		
		return userSettingsRepository.save(userSettings);
	}
}