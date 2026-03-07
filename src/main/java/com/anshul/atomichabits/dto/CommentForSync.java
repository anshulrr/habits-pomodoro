package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface CommentForSync {

	Long getId();

	UUID getPublicId();

	String getDescription();
	
	Long getCategoryId();
	
	Long getProjectId();

	Long getTaskId();

	String getCategory();
	
	String getProject();

	String getColor();
	
	String getTask();

	Instant getReviseDate();
	
	Instant getCreatedAt();

	Instant getUpdatedAt();
}
