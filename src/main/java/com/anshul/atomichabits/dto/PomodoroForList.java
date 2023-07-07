package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;

public interface PomodoroForList {

	Long getId();

	OffsetDateTime getStartTime();

	OffsetDateTime getEndTime();

	Integer getTimeElapsed();

	String getTask();
}
