package com.anshul.atomichabits.dto;

import java.time.Instant;

public interface TaskForNotifications {

	String getDescription();
	
	String getType();
	
	Instant getDueDate();
	
	String getEmail();
}
