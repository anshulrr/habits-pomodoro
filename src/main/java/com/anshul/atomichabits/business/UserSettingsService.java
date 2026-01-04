package com.anshul.atomichabits.business;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.dto.UserSettingsRequestDto;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.UserSettingsRepository;
import com.anshul.atomichabits.model.UserSettings;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserSettingsService {

	private UserSettingsRepository userSettingsRepository;
	
	public UserSettings retriveUserSettings(Long userId) {
		UserSettings userSettings = userSettingsRepository.findUserSettings(userId);
		
		if (userSettings == null) {
			throw new ResourceNotFoundException("user not found");
		}
		
		return userSettings;
	}
	
	public UserSettings updateUserSettings(Long userId, UserSettingsRequestDto settingsRequest) {
		UserSettings userSettings = userSettingsRepository.findUserSettings(userId);
		
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
		
		userSettings.setEnableChartYearlyAverage(settingsRequest.isEnableChartYearlyAverage());
		userSettings.setChartYearlyAverage(settingsRequest.getChartYearlyAverage());
		
		userSettings.setEnableChartAdjustedWeeklyMonthlyAverage(settingsRequest.isEnableChartAdjustedWeeklyMonthlyAverage());
		
		userSettings.setPageProjectsCount(settingsRequest.getPageProjectsCount());
		userSettings.setPageTasksCount(settingsRequest.getPageTasksCount());
		userSettings.setPageCommentsCount(settingsRequest.getPageCommentsCount());
		
		userSettings.setTasksChartType(settingsRequest.getTasksChartType());
		userSettings.setProjectsChartType(settingsRequest.getProjectsChartType());
		userSettings.setProjectCategoriesChartType(settingsRequest.getProjectCategoriesChartType());
		
		userSettings.setDefaultStatsLimit(settingsRequest.getDefaultStatsLimit());
		
		userSettings.setHomePageDefaultList(settingsRequest.getHomePageDefaultList());
		
		userSettings.setEnableNotifications(settingsRequest.isEnableNotifications());
		
		return userSettingsRepository.save(userSettings);
	}
}
