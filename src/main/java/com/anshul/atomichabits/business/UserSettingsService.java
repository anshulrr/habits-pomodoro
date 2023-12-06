package com.anshul.atomichabits.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.dto.UserSettingsRequestDto;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.UserSettingsRepository;
import com.anshul.atomichabits.model.UserSettings;

@Service
public class UserSettingsService {

	@Autowired
	private UserSettingsRepository userSettingsRepository;
	
	public UserSettings retriveUserSettings(Long user_id) {
		UserSettings userSettings = userSettingsRepository.findUserSettings(user_id);
		
		if (userSettings == null) {
			throw new ResourceNotFoundException("user not found");
		}
		
		return userSettings;
	}
	
	public UserSettings updateUserSettings(Long user_id, UserSettingsRequestDto settingsRequest) {
		UserSettings userSettings = userSettingsRepository.findUserSettings(user_id);
		
		userSettings.setPomodoroLength(settingsRequest.getPomodoroLength());
		userSettings.setBreakLength(settingsRequest.getBreakLength());
		
		userSettings.setEnableStopwatch(settingsRequest.isEnableStopwatch());
		userSettings.setEnableStopwatchAudio(settingsRequest.isEnableStopwatchAudio());

		userSettings.setEnableAutoStartBreak(settingsRequest.isEnableAutoStartBreak());
		userSettings.setEnableAutoTimerFullscreen(settingsRequest.isEnableAutoTimerFullscreen());
		
		userSettings.setEnableChartScale(settingsRequest.isEnableChartScale());
		userSettings.setChartScale(settingsRequest.getChartScale());
		
		userSettings.setEnableChartWeeklyAverage(settingsRequest.isEnableChartWeeklyAverage());
		userSettings.setChartWeeklyAverage(settingsRequest.getChartWeeklyAverage());
		
		userSettings.setEnableChartMonthlyAverage(settingsRequest.isEnableChartMonthlyAverage());
		userSettings.setChartMonthlyAverage(settingsRequest.getChartMonthlyAverage());
		
		userSettings.setEnableChartAdjustedWeeklyMonthlyAverage(settingsRequest.isEnableChartAdjustedWeeklyMonthlyAverage());
		
		userSettings.setPageProjectsCount(settingsRequest.getPageProjectsCount());
		userSettings.setPageTasksCount(settingsRequest.getPageTasksCount());
		userSettings.setPageCommentsCount(settingsRequest.getPageCommentsCount());
		
		userSettings.setTasksChartType(settingsRequest.getTasksChartType());
		userSettings.setProjectsChartType(settingsRequest.getProjectsChartType());
		userSettings.setProjectCategoriesChartType(settingsRequest.getProjectCategoriesChartType());
		
		userSettings.setHomePageDefaultList(settingsRequest.getHomePageDefaultList());
		
		return userSettingsRepository.save(userSettings);
	}
}
