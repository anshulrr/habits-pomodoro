package com.anshul.atomichabits.dto;

import java.time.Instant;

public record TaskFilter(
	Long projectId,
	Long tagId,
	Instant startDate, 
	Instant endDate,
	String searchString
) {}