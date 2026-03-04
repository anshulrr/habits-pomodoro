package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface CommentForList {

	Long getId();

	UUID getPublicId();

	String getDescription();
	
	String getCategory();
	
	String getProject();

	String getColor();
	
	String getTask();
	
	Instant getReviseDate();
	
	Instant getCreatedAt();

	Instant getUpdatedAt();
}
