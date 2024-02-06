package com.anshul.atomichabits.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users_settings")
public class UserSettings {

	@Id
	@GeneratedValue
	private Long id;

	// in minutes
	@Column(columnDefinition = "integer default 25")
	private Integer pomodoroLength = 25;
	
	@Column(columnDefinition = "integer default 5")
	private Integer breakLength = 5;

	@Column(columnDefinition = "boolean default false")
	private boolean enableStopwatch = false;

	@Column(columnDefinition = "boolean default false")
	private boolean enableStopwatchAudio = false;
	
	@Column(columnDefinition = "boolean default false")
	private boolean enableAutoStartBreak = false;
	
	@Column(columnDefinition = "boolean default true")
	private boolean enableAutoTimerFullscreen = true;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartScale = false;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartWeeklyAverage = false;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartMonthlyAverage = false;
	
	@Column(columnDefinition = "boolean default false")
	private boolean enableChartAdjustedWeeklyMonthlyAverage = false;

	@Column(columnDefinition = "integer default 25")
	private Integer chartScale = 25;

	@Column(columnDefinition = "integer default 7")
	private Integer chartWeeklyAverage = 7;

	@Column(columnDefinition = "integer default 30")
	private Integer chartMonthlyAverage = 30;
	
	@Column(columnDefinition = "integer default 5")
	private Integer pageProjectsCount = 5;
	
	@Column(columnDefinition = "integer default 5")
	private Integer pageTasksCount = 5;
	
	@Column(columnDefinition = "integer default 5")
	private Integer pageCommentsCount = 5;
	
	// TODO: use enum
	// bar, doughnut
	@Column(columnDefinition = "varchar(255) default 'doughnut'")
	private String tasksChartType = "doughnut";
	
	@Column(columnDefinition = "varchar(255) default 'bar'")
	private String projectsChartType = "bar";

	@Column(columnDefinition = "varchar(255) default 'bar'")
	private String projectCategoriesChartType = "bar";
	
	// projects, tags, filters
	@Column(columnDefinition = "varchar(10) default 'projects'")
	private String homePageDefaultList = "projects";
	
	@Column(columnDefinition = "boolean default false")
	private boolean enableNotifications = false;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	// @PrimaryKeyJoinColumn	// to make sure only single mapping for user
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public UserSettings(User user) {
		this.user = user;
	}
}
