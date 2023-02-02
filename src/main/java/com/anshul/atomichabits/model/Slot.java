package com.anshul.atomichabits.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name="slots")
public class Slot {
	
	Slot() {
		
	}

	@Id
	@GeneratedValue
	private Long id;
	
	private LocalDate startTime;
	private LocalDate endTime;
	// in minutes
	private Integer length;
	// in seconds
	private Integer timeElapsed;
	
	// started, paused, completed, discarded
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Task task;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	
} 
