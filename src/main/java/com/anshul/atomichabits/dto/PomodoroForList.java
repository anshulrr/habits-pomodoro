package com.anshul.atomichabits.dto;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PomodoroForList {

	UUID getId();

	String getStatus();

	@JsonIgnore
	OffsetDateTime getOffsetStartTime();

	@JsonIgnore
	OffsetDateTime getOffsetEndTime();
	
	default Instant getStartTime() {
        return getOffsetStartTime() != null ? getOffsetStartTime().toInstant() : null;
    }
	
	default Instant getEndTime() {
        return getOffsetStartTime() != null ? getOffsetStartTime().toInstant() : null;
    }

	Integer getTimeElapsed();

	UUID getTaskId();
	
	Instant getUpdatedAt();
	
	UUID getProjectId();
	
	UUID getCategoryId();
}
