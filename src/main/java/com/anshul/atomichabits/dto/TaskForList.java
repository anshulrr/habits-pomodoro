package com.anshul.atomichabits.dto;

import java.time.Instant;

import com.anshul.atomichabits.model.Project;

public interface TaskForList {

	Long getId();

	String getDescription();
	
	String getStatus();
	
	Integer getPomodoroLength();
	
	Instant getDueDate();
	
	Integer getPriority();
	
	Project getProject();
}
