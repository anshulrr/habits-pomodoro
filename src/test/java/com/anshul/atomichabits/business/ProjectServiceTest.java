package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.times;
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
import com.anshul.atomichabits.jpa.CommentRepository;
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
	
	@Mock
	private CommentRepository commentRepositoryMock;
	
	static User user;
	static ProjectCategory category;
	static ProjectCategory category2;
	
	static Long USER_ID = 1L;
	static Long CATEGORY_ID = 11L;
	static Long PROJECT_ID = 111L;
	
	@BeforeAll
	static void setup() {
		user = new User("Samay", "samay@xyz.com");
		category = new ProjectCategory(CATEGORY_ID, "Sample Category", user);
		category2 = new ProjectCategory(CATEGORY_ID + 1, "Sample Category 2", user);
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
		String status = "current";
		when(projectRepositoryMock.findUserProjects(USER_ID, status, 0, 0))
			.thenReturn(new ArrayList<ProjectForList>());
		
		List<ProjectForList> projects = projectService.retrieveAllProjects(USER_ID, status, 0, 0);
		
		assertEquals(0, projects.size());
	}
	
	@Test
	void retrieveAllProjectsCount() {
		String status = "current";
		when(projectRepositoryMock.getUserProjectsCount(USER_ID, status))
			.thenReturn(0);
		
		Integer projectsCount = projectService.retrieveProjectsCount(USER_ID, status);
		
		assertEquals(0, projectsCount);
	}
	
	@Test
	void retrieveCategoryProjects() {
		when(projectRepositoryMock.findCategoryProjects(USER_ID, CATEGORY_ID))
			.thenReturn(new ArrayList<ProjectForList>());
		
		List<ProjectForList> projects = projectService.retrieveCategoryProjects(USER_ID, CATEGORY_ID);
		
		assertEquals(0, projects.size());
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
	void updateProjectChangeCategory() {
		Project project = new Project(PROJECT_ID, "Test Project", user, category);
		
		when(projectRepositoryMock.findUserProjectById(USER_ID, PROJECT_ID))
			.thenReturn(Optional.of(project));
		when(projectCategoryRepositoryMock.findUserProjectCategoryById(USER_ID, CATEGORY_ID + 1))
			.thenReturn(Optional.of(category2));
		
		ProjectDto projectDtoRequest = new ProjectDto(project);
		projectDtoRequest.setPriority(5);
		projectDtoRequest.setProjectCategoryId(CATEGORY_ID + 1);
		
		projectService.updateProject(USER_ID, PROJECT_ID, projectDtoRequest);
		
		ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
		verify(projectRepositoryMock).save(captor.capture());
		
		assertEquals(project, captor.getValue());
		assertEquals(5, captor.getValue().getPriority());
		verify(commentRepositoryMock, times(1)).updateCommentsCategory(USER_ID, PROJECT_ID, CATEGORY_ID + 1);
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
