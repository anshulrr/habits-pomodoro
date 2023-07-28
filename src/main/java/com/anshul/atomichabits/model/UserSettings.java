package com.anshul.atomichabits.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "users_settings")
public class UserSettings {

	public UserSettings() {
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(columnDefinition = "boolean default false")
	private boolean enableStopWatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isEnableStopWatch() {
		return enableStopWatch;
	}

	public void setEnableStopWatch(boolean enableStopWatch) {
		this.enableStopWatch = enableStopWatch;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
