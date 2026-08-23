package com.anshul.atomichabits.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.anshul.atomichabits.model.TaskStatus;

@Component
public class StringToTaskStatusConverter implements Converter<String, TaskStatus> {

	@Override
	public TaskStatus convert(String source) {
		return TaskStatus.fromValue(source);
	}
}
