package com.anshul.atomichabits.dto;

public interface TaskForList {

	Long getId();

	String getDescription();
	
	String getStatus();
	
	Integer getPomodoroLength();
	
	Integer getPriority();
	
	String getPomodorosTimeElapsed();
}
