package com.anshul.atomichabits.model.converter;

import com.anshul.atomichabits.model.TaskType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaskTypeConverter implements AttributeConverter<TaskType, String> {

	@Override
	public String convertToDatabaseColumn(TaskType type) {
		return type == null ? null : type.getValue();
	}

	@Override
	public TaskType convertToEntityAttribute(String value) {
		return value == null ? null : TaskType.fromValue(value);
	}
}
