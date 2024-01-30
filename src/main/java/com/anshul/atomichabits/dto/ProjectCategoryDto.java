package com.anshul.atomichabits.dto;

import com.anshul.atomichabits.model.ProjectCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectCategoryDto {
	
	public ProjectCategoryDto(ProjectCategory category) {
		super();
		this.name = category.getName();
		this.level = category.getLevel();
		this.color = category.getColor();
		this.statsDefault = category.isStatsDefault();
		this.visibleToPartners = category.isVisibleToPartners();
	}
	
	@NotBlank
	private String name;
	
	@NotNull
	@Positive
	private Integer level;
	
	private boolean statsDefault;
	
	private boolean visibleToPartners;
	
	@NotBlank
	private String color;
	
	public ProjectCategory getProjectCategory() {
		ProjectCategory category = new ProjectCategory();
		
		category.setName(name);
		category.setLevel(level);
		category.setColor(color);
		category.setStatsDefault(statsDefault);
		category.setVisibleToPartners(visibleToPartners);
		
		return category;
	}
}
