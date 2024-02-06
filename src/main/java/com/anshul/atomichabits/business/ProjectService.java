package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.dto.ProjectDto;
import com.anshul.atomichabits.dto.ProjectForList;
import com.anshul.atomichabits.jpa.CommentRepository;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class ProjectService {

	private UserRepository userRepository;
	
	private ProjectRepository projectRepository;
	
	private ProjectCategoryRepository projectCategoryRepository;

	private CommentRepository commentRepository;
	
	public ProjectDto retriveProject(Long userId, Long id) {
		Optional<Project> projectEntry = projectRepository.findUserProjectById(userId, id);
		if (projectEntry.isEmpty())
			 	throw new ResourceNotFoundException("project id:" + id);
		
		return new ProjectDto(projectEntry.get());
	}
	
	public List<ProjectForList> retrieveAllProjects(Long userId, String status, int limit, int offset) {
		// TODO: using PageRequest
		return projectRepository.findUserProjects(userId, status, limit, offset);
	}

	public List<ProjectForList> retrieveCategoryProjects(Long userId, Long categoryId) {
		return projectRepository.findCategoryProjects(userId, categoryId);
	}
	
	public Integer retrieveProjectsCount(Long userId, String status) {
		return projectRepository.getUserProjectsCount(userId, status);
	}
	
	public ProjectDto createProject(Long userId, ProjectDto projectDto) {
		Optional<User> userEntry = userRepository.findById(userId);
		if (userEntry.isEmpty())
		 	throw new ResourceNotFoundException("user id: " + userId);

		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(userId,
				projectDto.getProjectCategoryId());
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + projectDto.getProjectCategoryId());
		
		Project project = new Project();

		project.setName(projectDto.getName());
		project.setDescription(projectDto.getDescription());
		project.setColor(projectDto.getColor());
		project.setPomodoroLength(projectDto.getPomodoroLength());
		project.setPriority(projectDto.getPriority());
		project.setType(projectDto.getType());
		project.setDailyLimit(projectDto.getDailyLimit());
		project.setUser(userEntry.get());
		project.setProjectCategory(categoryEntry.get());
		projectRepository.save(project);

		return new ProjectDto(project);
	}
	
	@Transactional
	public Project updateProject(Long userId, Long id, ProjectDto projectDto) {
		Optional<Project> projectEntry = projectRepository.findUserProjectById(userId, id);
		if (projectEntry.isEmpty())
			throw new ResourceNotFoundException("project id:" + id);
		
		Optional<ProjectCategory> category = projectCategoryRepository.findUserProjectCategoryById(userId,
				projectDto.getProjectCategoryId());
		
		log.trace("project for update: " + projectEntry + projectDto + category);
		
		if (projectEntry.get().getProjectCategory().getId() != category.get().getId()) {			
			// handle comments table for category update
			// @Transactional and @Modifying is required for update query
			// TODO: check if better solution is possible
			commentRepository.updateCommentsCategory(userId, id, category.get().getId());
		}

		projectEntry.get().setName(projectDto.getName());
		projectEntry.get().setDescription(projectDto.getDescription());
		projectEntry.get().setColor(projectDto.getColor());
		projectEntry.get().setPomodoroLength(projectDto.getPomodoroLength());
		projectEntry.get().setPriority(projectDto.getPriority());
		projectEntry.get().setType(projectDto.getType());
		projectEntry.get().setDailyLimit(projectDto.getDailyLimit());
		projectEntry.get().setProjectCategory(category.get());
		
		projectRepository.save(projectEntry.get());
		log.trace("updated project: {}", projectEntry);

		return projectEntry.get();
	}
}
