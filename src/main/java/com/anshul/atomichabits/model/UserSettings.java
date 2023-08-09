package com.anshul.atomichabits.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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

	@Column(columnDefinition = "integer default 25")
	private Integer chartScale = 25;

	@Column(columnDefinition = "integer default 5")
	private Integer chartWeeklyAverage = 5;

	@Column(columnDefinition = "integer default 22")
	private Integer chartMonthlyAverage = 22;

	@PrimaryKeyJoinColumn	// to make sure only single mapping for user
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public UserSettings(User user) {
		this.user = user;
	}
}
