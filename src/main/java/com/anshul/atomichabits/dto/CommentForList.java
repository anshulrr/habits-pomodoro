package com.anshul.atomichabits.dto;

import java.time.Instant;

public interface CommentForList {

	Long getId();

	String getDescription();
	
	String getCategory();
	
	String getProject();

	String getColor();
	
	String getTask();
	
	Instant getReviseDate();
	
	Instant getCreatedAt();
}
