package com.anshul.atomichabits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectCategoryDto {
	
	@NotBlank
	private String name;
	
	@NotNull
	private Integer level;
	
	private boolean statsDefault;
	
	private boolean visibleToPartners;
	
	private String color;
}
