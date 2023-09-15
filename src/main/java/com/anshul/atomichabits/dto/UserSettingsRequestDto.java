package com.anshul.atomichabits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

public 
@Getter
@Setter
class UserSettingsRequestDto {

	@NotNull @Positive
	Integer pomodoroLength;
	
	@NotNull @PositiveOrZero
	Integer breakLength;

	@NotNull
	boolean enableStopwatch;

	@NotNull
	boolean enableStopwatchAudio;
	
	@NotNull
	boolean enableAutoStartBreak;
	
	@NotNull
	boolean enableAutoTimerFullscreen;

	@NotNull
	boolean enableChartScale;

	@NotNull
	boolean enableChartWeeklyAverage;

	@NotNull
	boolean enableChartMonthlyAverage;
	
	@NotNull
	boolean enableChartAdjustedWeeklyMonthlyAverage;

	@NotNull @Positive
	Integer chartScale;

	@NotNull @Positive
	Integer chartWeeklyAverage;

	@NotNull @Positive
	Integer chartMonthlyAverage;
	
	@NotNull @Positive
	Integer pageProjectsCount;
	
	@NotNull @Positive
	Integer pageTasksCount;
	
	@NotNull @Positive
	Integer pageCommentsCount;
	
	@NotBlank
	String tasksChartType;
	
	@NotBlank
	String projectsChartType;
}
