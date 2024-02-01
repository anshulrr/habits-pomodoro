package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.anshul.atomichabits.dto.ProjectCategoryDto;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

@ExtendWith(MockitoExtension.class)
class ProjectCategoryServiceTest {

	@InjectMocks
	private ProjectCategoryService projectCategoryService;

	@Mock
	private UserRepository userRepositoryMock;
	
	@Mock
	private ProjectCategoryRepository projectCategoryRepositoryMock;
	
	static Long USER_ID = 1L;
	static Long CATEGORY_ID = 11L;
	static User user = new User("Samay", "samay@xyz.com");

	@Test
	void retriveProjectCategory() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.of(new ProjectCategory()));
		
		ProjectCategory projectCategory = projectCategoryService.retriveProjectCategory(USER_ID, CATEGORY_ID);
		
		assertNotNull(projectCategory);
	}
	
	@Test
	void retriveProjectCategoryEmpty() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.ofNullable(null));
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectCategoryService.retriveProjectCategory(USER_ID, CATEGORY_ID);
	    });
	    assertEquals("project category id:" + CATEGORY_ID, exception.getMessage());
	}
	
	@Test
	void retrieveAllProjectCategories() {
		int limit = 5;
		int offset = 0;
		
		List<ProjectCategory> categories = new ArrayList<>();
		categories.add(new ProjectCategory());
		
		when(projectCategoryRepositoryMock.findUserProjectCategories(USER_ID, limit, offset))
			.thenReturn(categories);
		
		List<ProjectCategory> retrievedCategories = projectCategoryService.retrieveAllProjectCategories(USER_ID, limit, offset);
		
		assertEquals(1, retrievedCategories.size());
	}
	
	@Test
	void retrieveAllProjectCategoriesCount() {
		when(projectCategoryRepositoryMock.getUserProjectCategoriesCount(USER_ID))
			.thenReturn(4);
		
		Integer categoriesCount = projectCategoryService.retrieveAllProjectCategoriesCount(USER_ID);
		
		assertEquals(4, categoriesCount);
	}
	
	@Test
	void createProjectCategory() {
		when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));
		
		ProjectCategoryDto projectCategoryRequest = new ProjectCategoryDto("category 1", 1, true, true, "#afafaf");
		
		projectCategoryService.createProjectCategory(USER_ID, projectCategoryRequest);
		
		ArgumentCaptor<ProjectCategory> captor = ArgumentCaptor.forClass(ProjectCategory.class);
		verify(projectCategoryRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
		assertEquals("#afafaf", captor.getValue().getColor());
	}
	
	@Test
	void updateProjectCategory() {
		ProjectCategory projectCategory = new ProjectCategory();
		projectCategory.setLevel(2);
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.of(projectCategory));
		
		ProjectCategoryDto projectCategoryRequest = new ProjectCategoryDto("category 1", 1, true, true, "#afafaf");
		
		projectCategoryService.updateProjectCategory(USER_ID, CATEGORY_ID, projectCategoryRequest);
		
		ArgumentCaptor<ProjectCategory> captor = ArgumentCaptor.forClass(ProjectCategory.class);
		verify(projectCategoryRepositoryMock).save(captor.capture());
		
		assertEquals(projectCategory, captor.getValue());
		assertEquals(1, captor.getValue().getLevel());
	}
	
	@Test
	void updateProjectCategoryEmpty() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.ofNullable(null));
		
		ProjectCategoryDto projectCategoryRequest = new ProjectCategoryDto("category 1", 1, true, true, "#afafaf");
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectCategoryService.updateProjectCategory(USER_ID, CATEGORY_ID, projectCategoryRequest);
	    });
	    assertEquals("project category id:" + CATEGORY_ID, exception.getMessage());
	}
}
