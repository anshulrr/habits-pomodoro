package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;

public interface PomodoroForList {

	UUID getId();

	String getStatus();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();

	UUID getTaskId();
	
	Instant getUpdatedAt();
	
	UUID getProjectId();
	
	UUID getCategoryId();
}
