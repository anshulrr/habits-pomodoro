package com.anshul.atomichabits.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;

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
	private Integer pomodoroLength = 0;

	private Instant dueDate;

	// current, archived
	@Column(columnDefinition = "varchar(255) default 'current'")
	private String status = "current";

	// neutral, good, bad
	@Column(columnDefinition = "varchar(10) default 'neutral'")
	private String type = "neutral";
	
	@Column(columnDefinition = "integer default 1")
	private Integer priority = 1;
	
	@Column(columnDefinition = "integer default 0")
	private Integer repeatDays = 0;
	
	@Column(columnDefinition = "integer default 1")
	private Integer dailyLimit = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Project project;
	
	@ManyToMany(cascade = {
	        CascadeType.ALL
	    })
	@JoinTable(
	  name = "tasks_tags", 
	  joinColumns = @JoinColumn(name = "task_id"), 
	  inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags;
	
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
	
	// constructor used in unit tests	
	public Task(Long id, String description, User user, Project project) {
		super();
		this.id = id;
		this.description = description;
		this.user = user;
		this.project = project;
	}
}
