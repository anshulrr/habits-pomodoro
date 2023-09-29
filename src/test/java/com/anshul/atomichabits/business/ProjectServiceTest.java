package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
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

import com.anshul.atomichabits.dto.ProjectDto;
import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@InjectMocks
	private ProjectService projectService;

	@Mock
	private UserRepository userRepositoryMock;
	
	@Mock
	private ProjectCategoryRepository projectCategoryRepositoryMock;

	@Mock
	private ProjectRepository projectRepositoryMock;
	
	static User user;
	static ProjectCategory category;
	
	static Long USER_ID = 1L;
	static Long CATEGORY_ID = 11L;
	static Long PROJECT_ID = 111L;
	
	@BeforeAll
	static void setup() {
		user = new User("Samay", "samay@xyz.com");
		category = new ProjectCategory(CATEGORY_ID, "Sample Category", user);
	}
	
	@Test
	void retriveProject() {
		Project project = new Project(PROJECT_ID, "Test Project", user, category);
		
		when(projectRepositoryMock.findUserProjectById(USER_ID, PROJECT_ID))
			.thenReturn(Optional.of(project));
		
		ProjectDto projectDto = projectService.retriveProject(USER_ID, PROJECT_ID);
		
		assertNotNull(projectDto);
	}
	
	@Test
	void retriveProjectEmpty() {
		Long nil_project_id = 12L;
		when(projectRepositoryMock.findUserProjectById(USER_ID, nil_project_id))
			.thenReturn(Optional.ofNullable(null));
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectService.retriveProject(1L, nil_project_id);
	    });
		
	    assertEquals("project id:" + nil_project_id, exception.getMessage());
	}
	
	@Test
	void retrieveAllProjects() {
		when(projectRepositoryMock.findUserProjects(USER_ID, 0, 0))
			.thenReturn(new ArrayList<ProjectForList>());
		
		List<ProjectForList> projects = projectService.retrieveAllProjects(USER_ID, 0, 0);
		
		assertEquals(0, projects.size());
	}
	
	@Test
	void retrieveAllProjectsCount() {
		when(projectRepositoryMock.getUserProjectsCount(USER_ID))
			.thenReturn(0);
		
		Integer projectsCount = projectService.retrieveProjectsCount(USER_ID);
		
		assertEquals(0, projectsCount);
	}
	
	@Test
	void createProject() {
		when(userRepositoryMock.findById(1L))
			.thenReturn(Optional.of(user));
		
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.of(category));
		
		Project project = new Project(PROJECT_ID, "Test Project", user, category);
		ProjectDto projectDtoRequest = new ProjectDto(project);
		
		projectService.createProject(USER_ID, projectDtoRequest);
		
		ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
		verify(projectRepositoryMock).save(captor.capture());
		
		assertEquals(user, captor.getValue().getUser());
	}
	
	@Test
	void updateProject() {
		Project project = new Project(PROJECT_ID, "Test Project", user, category);
		
		when(projectRepositoryMock.findUserProjectById(USER_ID, PROJECT_ID))
			.thenReturn(Optional.of(project));
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID))
			.thenReturn(Optional.of(category));
		
		ProjectDto projectDtoRequest = new ProjectDto(project);
		projectDtoRequest.setPriority(5);
		
		projectService.updateProject(USER_ID, PROJECT_ID, projectDtoRequest);
		
		ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
		verify(projectRepositoryMock).save(captor.capture());
		
		assertEquals(project, captor.getValue());
		assertEquals(5, captor.getValue().getPriority());
	}
	
	@Test
	void updateProjectEmpty() {
		Long nil_project_id = 12L;
		when(projectRepositoryMock.findUserProjectById(USER_ID, nil_project_id))
			.thenReturn(Optional.ofNullable(null));
		
		Project project = new Project(PROJECT_ID, "Test Project", user, category);
		ProjectDto projectDtoRequest = new ProjectDto(project);
		
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			projectService.updateProject(USER_ID, nil_project_id, projectDtoRequest);
	    });
	    assertEquals("project id:" + nil_project_id, exception.getMessage());
	}
}
