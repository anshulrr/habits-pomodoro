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
import jakarta.persistence.PrimaryKeyJoinColumn;

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
	private boolean enableStopwatch;

	@Column(columnDefinition = "boolean default false")
	private boolean enableStopwatchAudio;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartScale;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartWeeklyAverage;

	@Column(columnDefinition = "boolean default false")
	private boolean enableChartMonthlyAverage;
	
	@Column(columnDefinition = "boolean default false")
	private boolean enableChartAdjustedWeeklyMonthlyAverage;

	@Column(columnDefinition = "integer default 25")
	private Integer chartScale = 25;

	@Column(columnDefinition = "integer default 7")
	private Integer chartWeeklyAverage = 7;

	@Column(columnDefinition = "integer default 30")
	private Integer chartMonthlyAverage = 30;
	
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
