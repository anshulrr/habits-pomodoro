package com.anshul.atomichabits.model;

import java.time.Instant;
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
@Entity(name = "projects")
@Table(indexes= {
		@Index(name="projects_status_index", columnList="status"),
		@Index(name="projects_project_category_index", columnList="project_category_id"),
		@Index(name="projects_user_index", columnList="user_id")
})
public class Project {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	private String description;

	@Column(columnDefinition = "varchar(255) default '#228B22'")
	private String color = "#228B22";

	// in minutes
	@Column(columnDefinition = "integer default 0")
	private Integer pomodoroLength = 0;
	
	// added, archived
	@Column(columnDefinition = "varchar(255) default 'added'")
	private String status = "added";
	
	@Column(columnDefinition = "integer default 1")
	private Integer priority = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private ProjectCategory projectCategory;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	@OneToMany(mappedBy = "project")
	@JsonIgnore
	private List<Task> tasks;

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + ", color=" + color
				+ ", pomodoroLength=" + pomodoroLength + ", projectCategory=" + projectCategory.getName() + "]";
	}

	// constructor used in unit tests	
	public Project(Long id, String name, User user, ProjectCategory projectCategory) {
		super();
		this.id = id;
		this.name = name;
		this.user = user;
		this.projectCategory = projectCategory;
	}
}
