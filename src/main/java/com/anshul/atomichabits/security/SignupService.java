package com.anshul.atomichabits.security;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.*;
import com.anshul.atomichabits.model.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SignupService {

	private UserRepository userRepository;
	private UserSettingsRepository userSettingsRepository;
	private AuthorityRepository authorityRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;
	private CommentRepository commentRepository;
	private TagRepository tagRepository;

	public User saveUser(String username, String email, String phone) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPhone(phone);	
		user.setPassword("default");
		user.setEnabled(true);

		User savedUser = userRepository.save(user);

		authorityRepository.save(new Authority(user, "user"));

		userSettingsRepository.save(new UserSettings(user));

		// Create few initial project categories for reference
		createInitialUserData(user);

		return savedUser;
	}

	private void createInitialUserData(User user) {
		ProjectCategory project_category = createInitialProjectCategories(user);
		Project project = createInitialProject(user, project_category);
		Task task = createInitialTask(user, project);

		createInitialComments(user, project_category, project, task);

		createInitialTags(user);
	}

	private void createInitialTags(User user) {
		Tag tag1 = new Tag();
		tag1.setName("daily");
		tag1.setColor("#00c0f0");
		tag1.setUser(user);
		tagRepository.save(tag1);

		Tag tag2 = new Tag();
		tag2.setName("imp");
		tag2.setColor("#046134");
		tag2.setPriority(2);
		tag2.setUser(user);
		tagRepository.save(tag2);
	}

	private ProjectCategory createInitialProjectCategories(User user) {
		// create two project category named General, Hobbies
		ProjectCategory projectCategory1 = new ProjectCategory();
		projectCategory1.setUser(user);
		projectCategory1.setName("General");
		projectCategory1.setColor("124f12");
		projectCategory1.setLevel(1);
		projectCategory1.setStatsDefault(true);
		ProjectCategory project_category = projectCategoryRepository.save(projectCategory1);

		ProjectCategory projectCategory2 = new ProjectCategory();
		projectCategory2.setUser(user);
		projectCategory2.setName("Health");
		projectCategory2.setColor("4a86e8");
		projectCategory2.setLevel(2);
		projectCategory2.setStatsDefault(true);
		projectCategoryRepository.save(projectCategory2);

		ProjectCategory projectCategory3 = new ProjectCategory();
		projectCategory3.setUser(user);
		projectCategory3.setName("Hobbies");
		projectCategory3.setColor("ff0000");
		projectCategory3.setLevel(3);
		projectCategory3.setStatsDefault(false);
		projectCategoryRepository.save(projectCategory3);

		ProjectCategory projectCategory4 = new ProjectCategory();
		projectCategory4.setUser(user);
		projectCategory4.setName("Rest");
		projectCategory4.setColor("919191");
		projectCategory4.setLevel(4);
		projectCategory4.setStatsDefault(false);
		projectCategory4.setVisibleToPartners(false);
		projectCategoryRepository.save(projectCategory4);

		return project_category;
	}

	private Project createInitialProject(User user, ProjectCategory project_category) {
		Project project = new Project();
		project.setUser(user);
		project.setProjectCategory(project_category);
		project.setName("General Project");
		project.setColor("#22688c");
		project.setPomodoroLength(0);
		return projectRepository.save(project);
	}

	private Task createInitialTask(User user, Project project) {
		Task task = new Task();
		task.setUser(user);
		task.setProject(project);
		task.setDescription("Sample Task");
		task.setPomodoroLength(0);
		return taskRepository.save(task);
	}

	private void createInitialComments(User user, ProjectCategory project_category, Project project, Task task) {
		Comment comment = new Comment();
		comment.setUser(user);
		comment.setDescription("""
				Sample note with some markdown syntax
				- line 1
				  - line 2
				  - [Google Search Page](https://www.google.com/)
				""");
		commentRepository.save(comment);

		Comment comment1 = new Comment();
		comment1.setUser(user);
		comment1.setProjectCategory(project_category);
		comment1.setDescription("Sample note of a Project Category");
		commentRepository.save(comment1);

		Comment comment2 = new Comment();
		comment2.setUser(user);
		comment2.setProjectCategory(project_category);
		comment2.setProject(project);
		comment2.setDescription("Sample note of a Project");
		commentRepository.save(comment2);

		Comment comment3 = new Comment();
		comment3.setUser(user);
		comment3.setProjectCategory(project_category);
		comment3.setProject(project);
		comment3.setTask(task);
		comment3.setDescription("Sample note of a Task");
		commentRepository.save(comment3);
	}
}
