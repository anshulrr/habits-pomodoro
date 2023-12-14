package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectCategoryRepository projectCategoryRepository;

	@Autowired 
	private CommentRepository commentRepository;
	
	public ProjectDto retriveProject(Long user_id, Long id) {
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, id);
		if (projectEntry.isEmpty())
			 	throw new ResourceNotFoundException("project id:" + id);
		
		return new ProjectDto(projectEntry.get());
	}
	
	public List<ProjectForList> retrieveAllProjects(Long user_id, String status, int limit, int offset) {
		// TODO: using PageRequest
		return projectRepository.findUserProjects(user_id, status, limit, offset);
	}

	public List<ProjectForList> retrieveCategoryProjects(Long user_id, Long category_id) {
		return projectRepository.findCategoryProjects(user_id, category_id);
	}
	
	public Integer retrieveProjectsCount(Long user_id, String status) {
		return projectRepository.getUserProjectsCount(user_id, status);
	}
	
	public ProjectDto createProject(Long user_id, ProjectDto projectDto) {
		Optional<User> userEntry = userRepository.findById(user_id);

		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id,
				projectDto.getProjectCategoryId());
		if (categoryEntry.isEmpty())
			throw new ResourceNotFoundException("project category id:" + projectDto.getProjectCategoryId());
		
		Project project = new Project();

		project.setName(projectDto.getName());
		project.setDescription(projectDto.getDescription());
		project.setColor(projectDto.getColor());
		project.setPomodoroLength(projectDto.getPomodoroLength());
		project.setPriority(projectDto.getPriority());
		project.setUser(userEntry.get());
		project.setProjectCategory(categoryEntry.get());
		projectRepository.save(project);

		return new ProjectDto(project);
	}
	
	@Transactional
	public Project updateProject(Long user_id, Long id, ProjectDto projectDto) {
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, id);
		if (projectEntry.isEmpty())
			throw new ResourceNotFoundException("project id:" + id);
		
		Optional<ProjectCategory> category = projectCategoryRepository.findUserProjectCategoryById(user_id,
				projectDto.getProjectCategoryId());
		
		log.trace("project for update: " + projectEntry + projectDto + category);
		
		if (projectEntry.get().getProjectCategory().getId() != category.get().getId()) {			
			// handle comments table for category update
			// @Transactional and @Modifying is required for update query
			// TODO: check if better solution is possible
			commentRepository.updateCommentsCategory(user_id, id, category.get().getId());
		}

		projectEntry.get().setName(projectDto.getName());
		projectEntry.get().setDescription(projectDto.getDescription());
		projectEntry.get().setColor(projectDto.getColor());
		projectEntry.get().setPomodoroLength(projectDto.getPomodoroLength());
		projectEntry.get().setPriority(projectDto.getPriority());
		projectEntry.get().setProjectCategory(category.get());
		projectEntry.get().setStatus(projectDto.getStatus());
		projectRepository.save(projectEntry.get());
		log.trace("updated project: {}", projectEntry);

		return projectEntry.get();
	}
}
