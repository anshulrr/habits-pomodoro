package com.anshul.atomichabits.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectCategoryDto {
	
	@NotNull
	private UUID publicId;
	
	@NotBlank
	private String name;
	
	@NotNull
	@Positive
	private Integer level;
	
	private boolean statsDefault;
	
	private boolean visibleToPartners;
	
	@NotBlank
	private String color;

}
