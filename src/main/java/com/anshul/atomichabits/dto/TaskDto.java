package com.anshul.atomichabits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TaskDto(Long id, @NotBlank String description, @NotNull @PositiveOrZero Integer pomodoroLength, @NotNull @Positive Integer priority, @NotBlank String status) {}