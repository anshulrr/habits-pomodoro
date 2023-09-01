package com.anshul.atomichabits.dto;

import java.time.Instant;

public interface TaskForList {

	Long getId();

	String getDescription();
	
	String getStatus();
	
	Integer getPomodoroLength();
	
	Instant getDueDate();
	
	Integer getPriority();
	
	String getPomodorosTimeElapsed();
}
