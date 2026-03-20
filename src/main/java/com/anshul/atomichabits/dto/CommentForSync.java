package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface CommentForSync {

	UUID getId();

	String getDescription();
	
	UUID getCategoryId();
	
	UUID getProjectId();

	UUID getTaskId();

	Instant getReviseDate();
	
	Instant getCreatedAt();

	Instant getUpdatedAt();
}
