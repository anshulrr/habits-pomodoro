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

// won't work for nested projection
// public record ProjectForList(Long id, String name, String color, String category) {}
