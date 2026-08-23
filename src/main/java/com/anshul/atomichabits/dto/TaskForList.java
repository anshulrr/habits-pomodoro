package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

import com.anshul.atomichabits.model.TaskStatus;
import com.anshul.atomichabits.model.TaskType;

public interface TaskForList {

	UUID getId();

	String getDescription();

	TaskStatus getStatus();

	TaskType getType();
	
	Integer getPomodoroLength();
	
	Instant getDueDate();
	
	Integer getRepeatDays();
	
	Integer getPriority();
	
	Integer getDailyLimit();
	
	boolean isEnableNotifications();

	Instant getUpdatedAt();
	
	UUID getProjectId();
	
	Long getProjectPriority();
	
	Long getCategoryPriority();
	
}
