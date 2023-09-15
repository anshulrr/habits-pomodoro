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

	@Test
	void retriveProjectCategory() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(1L, 2L))
			.thenReturn(Optional.of(new ProjectCategory()));
		
		ProjectCategory projectCategory = projectCategoryService.retriveProjectCategory(1L, 2L);
		
		assertNotNull(projectCategory);
	}
	
	@Test
	void retriveProjectCategoryEmpty() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(1L, 2L))
			.thenReturn(Optional.ofNullable(null));
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectCategoryService.retriveProjectCategory(1L, 2L);
	    });
	    assertEquals("project category id:" + 2L, exception.getMessage());
	}
	
	@Test
	void retrieveAllProjectCategories() {
		when(projectCategoryRepositoryMock.findUserProjectCategories(1L, 0, 0))
			.thenReturn(new ArrayList<ProjectCategory>());
		
		List<ProjectCategory> categories = projectCategoryService.retrieveAllProjectCategories(1L, 0, 0);
		
		assertEquals(0, categories.size());
	}
	
	@Test
	void retrieveAllProjectCategoriesCount() {
		when(projectCategoryRepositoryMock.getUserProjectCategoriesCount(1L))
			.thenReturn(0);
		
		Integer categoriesCount = projectCategoryService.retrieveAllProjectCategoriesCount(1L);
		
		assertEquals(0, categoriesCount);
	}
	
	@Test
	void createProjectCategory() {
		User user = new User("Samay", "samay@xyz.com");
		
		when(userRepositoryMock.findById(1L)).thenReturn(
				Optional.of(user)
				);
		
		ProjectCategory projectCategoryRequest = new ProjectCategory();
		
		projectCategoryService.createProjectCategory(1L, projectCategoryRequest);
		
		ArgumentCaptor<ProjectCategory> captor = ArgumentCaptor.forClass(ProjectCategory.class);
		verify(projectCategoryRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
	}
	
	@Test
	void updateProjectCategory() {
		ProjectCategory projectCategory = new ProjectCategory();
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(1L, 2L))
			.thenReturn(Optional.of(projectCategory));
		
		ProjectCategory projectCategoryRequest = new ProjectCategory();
		projectCategoryRequest.setLevel(0);
		
		projectCategoryService.updateProjectCategory(1L, 2L, projectCategoryRequest);
		
		ArgumentCaptor<ProjectCategory> captor = ArgumentCaptor.forClass(ProjectCategory.class);
		verify(projectCategoryRepositoryMock).save(captor.capture());
		
		assertEquals(projectCategory, captor.getValue());
		assertEquals(0, captor.getValue().getLevel());
	}
	
	@Test
	void updateProjectCategoryEmpty() {
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(1L, 2L))
			.thenReturn(Optional.ofNullable(null));
		
		ProjectCategory projectCategoryRequest = new ProjectCategory();
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectCategoryService.updateProjectCategory(1L, 2L, projectCategoryRequest);
	    });
	    assertEquals("project category id:" + 2L, exception.getMessage());
	}
}
