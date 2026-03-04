package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;

public interface PomodoroForList {

	Long getId();

	UUID getPublicId();
	
	String getStatus();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();

	Long getTaskId();
	
	String getTask();
	
	String getColor();

	Instant getUpdatedAt();
	
	String getProjectId();
}
