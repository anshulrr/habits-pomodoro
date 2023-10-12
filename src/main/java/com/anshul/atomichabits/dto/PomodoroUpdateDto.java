package com.anshul.atomichabits.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PomodoroUpdateDto(
	@Min(value = 0) Integer timeElapsed, 
	@NotBlank String status
) {}