package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;

public interface PomodoroForList {

	Long getId();
	
	String getStatus();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();

	String getTask();
	
	String getColor();
}
