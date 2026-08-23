package com.anshul.atomichabits.dto;

import java.time.Instant;

import com.anshul.atomichabits.model.TaskType;

public interface TaskForNotifications {

	String getDescription();

	TaskType getType();
	
	Instant getDueDate();
	
	String getEmail();
}
