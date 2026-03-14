package com.anshul.atomichabits.dto;

import java.util.UUID;

public interface ProjectForList {

	UUID getId();

	String getName();

	String getColor();

	String getDescription();

	Integer getPomodoroLength();

	Integer getPriority();

	String getCategory();

	String getCategoryColor();

	String getType();

	Integer getDailyLimit();
	
	UUID getProjectCategoryId();
	
	Long getCategoryPriority();
}
