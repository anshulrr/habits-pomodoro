package com.anshul.atomichabits.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {

	CURRENT("current"),
	ARCHIVED("archived");

	private final String value;

	TaskStatus(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static TaskStatus fromValue(String value) {
		for (TaskStatus status : values()) {
			if (status.value.equals(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Unknown task status: " + value);
	}

	@Override
	public String toString() {
		return value;
	}
}
