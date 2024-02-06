package com.anshul.atomichabits.dto;

public interface ProjectForList {

	Long getId();

	String getName();

	String getColor();

	String getDescription();

	Integer getPomodoroLength();

	Integer getPriority();

	String getCategory();

	String getCategoryColor();

	String getType();

	Integer getDailyLimit();
}
