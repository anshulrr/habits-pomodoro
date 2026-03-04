package com.anshul.atomichabits.dto;

import java.util.UUID;

public interface ProjectForList {

	Long getId();

	UUID getPublicId();

	String getName();

	String getColor();

	String getDescription();

	Integer getPomodoroLength();

	Integer getPriority();

	String getCategory();

	String getCategoryColor();

	String getType();

	Integer getDailyLimit();
	
	Long getProjectCategoryId();
	
	Long getCategoryPriority();
}
