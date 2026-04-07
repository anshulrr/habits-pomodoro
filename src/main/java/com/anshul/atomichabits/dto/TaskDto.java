package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TaskDto(
		@NotNull UUID id, 
		@NotBlank String description, 
		@NotNull @PositiveOrZero Integer pomodoroLength, 
		Instant dueDate, 
		@NotNull @PositiveOrZero Integer repeatDays, 
		@NotNull @PositiveOrZero Integer dailyLimit, 
		@NotBlank String status,
		@NotBlank String type,
		@NotNull boolean enableNotifications,
		@NotNull Instant updatedAt,
		@NotNull UUID projectId,
		List<UUID> tagIds
	) {}