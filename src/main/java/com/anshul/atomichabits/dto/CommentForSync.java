package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface CommentForSync {

	UUID getId();

	String getDescription();
	
	UUID getCategoryId();
	
	UUID getProjectId();

	UUID getTaskId();

	String getCategory();
	
	String getProject();

	String getColor();
	
	String getTask();

	Instant getReviseDate();
	
	Instant getCreatedAt();

	Instant getUpdatedAt();
}
