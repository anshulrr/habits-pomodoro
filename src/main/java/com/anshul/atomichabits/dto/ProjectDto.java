package com.anshul.atomichabits.dto;

import com.anshul.atomichabits.model.Project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ProjectDto {

	public ProjectDto() {
	}
	
	public ProjectDto(Project project) {
		super();
		this.id = project.getId();
		this.name = project.getName();
		this.description = project.getDescription();
		this.color = project.getColor();
		this.pomodoroLength = project.getPomodoroLength();
	}

	private Long id;
	
	@NotBlank
	private String name;
	
	private String description;
	
	@NotBlank
	private String color;
	
	@PositiveOrZero
	private Integer pomodoroLength;
	
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

	public Integer getPomodoroLength() {
		return pomodoroLength;
	}

	public void setPomodoroLength(Integer pomodoroLength) {
		this.pomodoroLength = pomodoroLength;
	}

	@Override
	public String toString() {
		return "ProjectDto [id=" + id + ", name=" + name + ", description=" + description + ", color=" + color
				+ ", pomodoroLength=" + pomodoroLength + "]";
	}
}
