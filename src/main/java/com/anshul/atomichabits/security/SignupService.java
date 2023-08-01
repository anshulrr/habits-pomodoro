package com.anshul.atomichabits.security;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.AuthorityRepository;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

@Service
public class SignupService {

	private UserRepository userRepository;
	private AuthorityRepository authorityRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;

	public SignupService(UserRepository r, AuthorityRepository a, ProjectCategoryRepository pc, ProjectRepository p, TaskRepository t) {
		this.userRepository = r;
		this.authorityRepository = a;
		this.projectCategoryRepository = pc;
		this.projectRepository = p;
		this.taskRepository = t;
	}

	public User saveUser(String email) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(email);
		user.setPassword("default");
		user.setEnabled(true);
		
		userRepository.save(user);

		authorityRepository.save(new Authority(user, "user"));
		
		// Create few initial project categories for reference
		createInitialUserData(user);

		return user;
	}
	
	private void createInitialUserData(User user) {
		ProjectCategory project_category = createInitialProjectCategories(user);
		Project project = createInitialProject(user, project_category);
		createInitialTask(user, project);
	}

	private ProjectCategory createInitialProjectCategories(User user) {
		// create two project category named General, Hobbies
		ProjectCategory projectCategory1 = new ProjectCategory();
		projectCategory1.setUser(user);
		projectCategory1.setName("General");
		projectCategory1.setLevel(1);
		ProjectCategory project_category = projectCategoryRepository.save(projectCategory1);

		ProjectCategory projectCategory2 = new ProjectCategory();
		projectCategory2.setUser(user);
		projectCategory2.setName("Health");
		projectCategory2.setLevel(2);
		projectCategoryRepository.save(projectCategory2);
		
		ProjectCategory projectCategory3 = new ProjectCategory();
		projectCategory3.setUser(user);
		projectCategory3.setName("Hobbies");
		projectCategory3.setLevel(3);
		projectCategoryRepository.save(projectCategory3);

		ProjectCategory projectCategory4 = new ProjectCategory();
		projectCategory4.setUser(user);
		projectCategory4.setName("Rest");
		projectCategory4.setLevel(4);
		projectCategoryRepository.save(projectCategory4);
		
		return project_category;
	}
	
	private Project createInitialProject(User user, ProjectCategory project_category) {
		Project project = new Project();
		project.setUser(user);
		project.setProjectCategory(project_category);
		project.setName("Sample Project");
		project.setColor("#22688c");
		project.setPomodoroLength(0);
		return projectRepository.save(project);
	}
	
	private void createInitialTask(User user, Project project) {
		Task task = new Task();
		task.setUser(user);
		task.setProject(project);
		task.setDescription("Sample Task");
		task.setPomodoroLength(0);
		task.setStatus("added");
		taskRepository.save(task);
	}
}
