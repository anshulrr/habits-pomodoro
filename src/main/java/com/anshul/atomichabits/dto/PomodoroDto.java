package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Task;

public interface PomodoroDto {

	UUID getId();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();
	
	Integer getLength();
	
	String getStatus();

	Task getTask();

	Instant getUpdatedAt();
	
	Project getProject();
}
