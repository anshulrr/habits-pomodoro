package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface TaskForList {

	UUID getId();
	
	String getDescription();
	
	String getStatus();
	
	String getType();
	
	Integer getPomodoroLength();
	
	Instant getDueDate();
	
	Integer getRepeatDays();
	
	Integer getPriority();
	
	Integer getDailyLimit();
	
	boolean isEnableNotifications();
	
	UUID getProjectId();
	
}
