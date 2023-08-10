package com.anshul.atomichabits.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private ProjectCategory projectCategory;

	@OneToMany(mappedBy = "project")
	@JsonIgnore
	private List<Task> tasks;

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + ", color=" + color
				+ ", pomodoroLength=" + pomodoroLength + ", projectCategory=" + projectCategory.getName() + "]";
	}
}
