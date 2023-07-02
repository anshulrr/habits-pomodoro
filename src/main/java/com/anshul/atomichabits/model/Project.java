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

@Entity(name="projects")
public class Project {
	
	Project() {
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	private String description;
	
	@Column(columnDefinition = "varchar(255) default 'blue'")
	private String color = "blue";
	
	// in minutes
	private Integer pomodoroLength;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
//	@JsonIgnore
	private ProjectCategory projectCategory;

	@OneToMany(mappedBy = "project")
	@JsonIgnore
	private List<Task> tasks;

	public Project(Long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	// dt what is use of this
	public void setUser(User user) {
		this.user = user;
	}
	
	public ProjectCategory getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(ProjectCategory projectCategory) {
		this.projectCategory = projectCategory;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
