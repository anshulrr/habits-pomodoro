package com.anshul.atomichabits.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskType {

	NEUTRAL("neutral"),
	GOOD("good"),
	BAD("bad");

	private final String value;

	TaskType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static TaskType fromValue(String value) {
		for (TaskType type : values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown task type: " + value);
	}

	@Override
	public String toString() {
		return value;
	}
}
