package com.anshul.atomichabits.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "pomodoros")
public class Pomodoro {

	Pomodoro() {

	}

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

	public Pomodoro(Long id, OffsetDateTime startTime, OffsetDateTime endTime, Integer length, Integer timeElapsed,
			String status, Task task, User user) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.length = length;
		this.timeElapsed = timeElapsed;
		this.status = status;
		this.task = task;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
	}

	public OffsetDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(OffsetDateTime endTime) {
		this.endTime = endTime;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(Integer timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Pomodoro [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", length=" + length
				+ ", timeElapsed=" + timeElapsed + ", status=" + status + ", task=" + task + ", user=" + user + "]";
	}

}
