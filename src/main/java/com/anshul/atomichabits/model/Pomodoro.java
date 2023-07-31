package com.anshul.atomichabits.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "pomodoros")
public class Pomodoro {

	@Id
	@GeneratedValue
	private Long id;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime startTime;
	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime endTime;

	// in minutes
	private Integer length = 25;
	// in seconds
	private Integer timeElapsed;

	// started, paused, completed, discarded
	private String status = "started";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Task task;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@Override
	public String toString() {
		return "Pomodoro [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", length=" + length
				+ ", timeElapsed=" + timeElapsed + ", status=" + status + ", task=" + task.getDescription()
				+ ", useremail=" + user.getEmail() + "]";
	}

}
