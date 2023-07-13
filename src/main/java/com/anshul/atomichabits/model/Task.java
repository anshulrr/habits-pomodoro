package com.anshul.atomichabits.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity(name = "tasks")
public class Task {

	Task() {

	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String description;

	private Integer estimatedPomodorosCount = 0;

	// in minutes
	@Column(columnDefinition = "integer default 0")
	private Integer pomodoroLength;

	private LocalDate dueDate;

	// added, finished
	@Column(columnDefinition = "varchar(255) default 'added'")
	private String status = "added";

	public Task(Long id, String description, Integer pomodoros, LocalDate dueDate, String status, User user) {
		super();
		this.id = id;
		this.description = description;
		this.estimatedPomodorosCount = pomodoros;
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
	private List<Pomodoro> pomodoros;

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

	public Integer getPomodoros() {
		return estimatedPomodorosCount;
	}

	public void setPomodoros(Integer pomodoros) {
		this.estimatedPomodorosCount = pomodoros;
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

	public Integer getPomodoroLength() {
		return pomodoroLength;
	}

	public void setPomodoroLength(Integer pomodoroLength) {
		this.pomodoroLength = pomodoroLength;
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
		return "Task [id=" + id + ", description=" + description + ", pomodoros count=" + estimatedPomodorosCount
				+ ", dueDate=" + dueDate + ", status=" + status + ", user=" + user + "]";
	}
}
