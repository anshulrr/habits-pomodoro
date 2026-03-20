package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public interface ProjectForList {

	UUID getId();

	String getName();

	String getColor();

	String getDescription();

	Integer getPomodoroLength();

	Integer getPriority();

	String getType();

	Integer getDailyLimit();
	
	Instant getUpdatedAt();
	
	UUID getProjectCategoryId();
	
	Long getCategoryPriority();
}
