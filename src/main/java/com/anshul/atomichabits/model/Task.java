package com.anshul.atomichabits.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tasks")
@Table(indexes= {
		@Index(name="tasks_status_index", columnList="status"),
		@Index(name="tasks_project_index", columnList="project_id"),
		@Index(name="tasks_user_index", columnList="user_id")
})
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

	// added, archived, completed
	@Column(columnDefinition = "varchar(255) default 'added'")
	private String status = "added";
	
	@Column(columnDefinition = "integer default 1")
	private Integer priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Project project;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	@OneToMany(mappedBy = "task")
	@JsonIgnore
	private List<Pomodoro> pomodoros;

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", pomodoros count=" + estimatedPomodorosCount
				+ ", dueDate=" + dueDate + ", status=" + status + ", user=" + user.getEmail() + "]";
	}
}
