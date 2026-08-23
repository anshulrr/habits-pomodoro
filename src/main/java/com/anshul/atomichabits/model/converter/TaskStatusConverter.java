package com.anshul.atomichabits.model.converter;

import com.anshul.atomichabits.model.TaskStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {

	@Override
	public String convertToDatabaseColumn(TaskStatus status) {
		return status == null ? null : status.getValue();
	}

	@Override
	public TaskStatus convertToEntityAttribute(String value) {
		return value == null ? null : TaskStatus.fromValue(value);
	}
}
