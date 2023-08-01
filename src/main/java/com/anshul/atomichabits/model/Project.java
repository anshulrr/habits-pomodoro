package com.anshul.atomichabits.model;

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
@Entity(name = "projects")
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
