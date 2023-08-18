package com.anshul.atomichabits.model;

import java.time.Instant;

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
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "comments")
@Table(indexes= {
		@Index(name="comments_status_index", columnList="status"),
		@Index(name="comments_user_index", columnList="user_id"),
		@Index(name="comments_project_category_index", columnList="project_category_id"),
		@Index(name="comments_project_index", columnList="project_id"),
		@Index(name="comments_task_index", columnList="task_id"),
		@Index(name="comments_pomodoro_index", columnList="pomodoro_id")		
})
public class Comment {

	@Id
	@GeneratedValue
	private Long id;

	@Column(columnDefinition = "text", nullable = false)
	private String description;

	// added, deleted
	@Column(columnDefinition = "varchar(255) default 'added'")
	private String status = "added";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private ProjectCategory projectCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Project project;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Task task;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Pomodoro pomodoro;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
}
