package com.anshul.atomichabits.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.anshul.atomichabits.model.TaskType;

@Component
public class StringToTaskTypeConverter implements Converter<String, TaskType> {

	@Override
	public TaskType convert(String source) {
		return TaskType.fromValue(source);
	}
}
