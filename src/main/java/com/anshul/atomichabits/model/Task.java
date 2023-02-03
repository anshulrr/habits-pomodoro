package com.anshul.atomichabits.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity(name="tasks")
public class Task {

	Task() {
		
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String description;
	
	private Integer estimatedSlotsCount = 0;
	
	// in minutes
	private Integer slotLength;
	
	private LocalDate dueDate;
	
	// added, finished
	private String status = "added";
	
	public Task(Long id, String description, Integer slots, LocalDate dueDate, String status, User user) {
		super();
		this.id = id;
		this.description = description;
		this.estimatedSlotsCount = slots;
		this.dueDate = dueDate;
		this.status = status;
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Project project;
	
	@OneToMany(mappedBy = "task")
	@JsonIgnore
	private List<Slot> slots;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSlots() {
		return estimatedSlotsCount;
	}

	public void setSlots(Integer slots) {
		this.estimatedSlotsCount = slots;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", slots=" + estimatedSlotsCount + ", dueDate=" + dueDate
				+ ", status=" + status + ", user=" + user + "]";
	}
}
