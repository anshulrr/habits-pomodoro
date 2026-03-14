package com.anshul.atomichabits.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskFilter(
	UUID projectId,
	UUID tagId,
	Instant startDate, 
	Instant endDate,
	String searchString
) {}