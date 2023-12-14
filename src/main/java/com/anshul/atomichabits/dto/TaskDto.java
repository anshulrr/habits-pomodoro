package com.anshul.atomichabits.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TaskDto(
		Long id, 
		@NotBlank String description, 
		@NotNull @PositiveOrZero Integer pomodoroLength, 
		Instant dueDate, 
		@NotNull @PositiveOrZero Integer repeatDays, 
		@NotNull @Positive Integer priority, 
		@NotBlank String status,
		@NotBlank String type,
		@NotNull @Positive Long projectId
	) {}