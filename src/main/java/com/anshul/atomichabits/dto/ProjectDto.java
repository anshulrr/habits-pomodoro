package com.anshul.atomichabits.dto;

import com.anshul.atomichabits.model.Project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ProjectDto {

	// For entity to dto mapping
	public ProjectDto(Project project) {
		super();
		this.id = project.getId();
		this.name = project.getName();
		this.description = project.getDescription();
		this.color = project.getColor();
		this.pomodoroLength = project.getPomodoroLength();
		this.priority = project.getPriority();
		this.status = project.getStatus();
		log.trace("mapping category id");
		this.projectCategoryId = project.getProjectCategory().getId();
	}

	private Long id;
	
	@NotBlank
	private String name;
	
	private String description;
	
	@NotBlank
	private String color;
	
	@NotNull
	@PositiveOrZero
	private Integer pomodoroLength;
	
	@NotNull
	@Positive
	private Integer priority;
	
	private String status = "current";
	
	private Long projectCategoryId;
	
	// TODO check: I think because of spring boot projection, getters is used to decide variable name in dto object
	public Long getProjectCategoryId() {
		return projectCategoryId;
	}

	public void setProjectCategoryId(Long projectCategoryId) {
		this.projectCategoryId = projectCategoryId;
	}

	@Override
	public String toString() {
		return "ProjectDto [id=" + id + ", name=" + name + ", description=" + description + ", color=" + color
				+ ", pomodoroLength=" + pomodoroLength + ", projectCategoryId=" + projectCategoryId + "]";
	}
}
