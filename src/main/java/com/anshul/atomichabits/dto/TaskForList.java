package com.anshul.atomichabits.dto;

import java.time.Instant;

public interface TaskForList {

	Long getId();

	String getDescription();
	
	String getStatus();
	
	String getType();
	
	Integer getPomodoroLength();
	
	Instant getDueDate();
	
	Integer getRepeatDays();
	
	Integer getPriority();
	
	Integer getDailyLimit();
	
	boolean isEnableNotifications();
	
	ProjectSummary getProject();
	
	interface ProjectSummary {
		
		Long getId();
		
		String getName();
		
		String getColor();
		
		Integer getPomodoroLength();
	}
}
