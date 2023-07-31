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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tasks")
public class Task {

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

	// added, completed
	@Column(columnDefinition = "varchar(255) default 'added'")
	private String status = "added";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Project project;

	@OneToMany(mappedBy = "task")
	@JsonIgnore
	private List<Pomodoro> pomodoros;

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", pomodoros count=" + estimatedPomodorosCount
				+ ", dueDate=" + dueDate + ", status=" + status + ", user=" + user.getEmail() + "]";
	}
}
