package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;

import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.Task;

public interface PomodoroDto {

	Long getId();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();
	
	Integer getLength();
	
	String getStatus();

	Task getTask();
	
	Project getProject();
}
