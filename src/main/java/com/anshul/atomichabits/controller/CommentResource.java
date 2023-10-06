package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.dto.CommentForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.CommentRepository;
import com.anshul.atomichabits.jpa.PomodoroRepository;
import com.anshul.atomichabits.jpa.ProjectCategoryRepository;
import com.anshul.atomichabits.jpa.ProjectRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Comment;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
public class CommentResource {

	private UserRepository userRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;
	private PomodoroRepository pomodoroRepository;
	private CommentRepository commentRepository;

	@GetMapping("/comments")
	public List<CommentForList> retrieveComments(Principal principal, 
			@RequestParam(defaultValue = "added") String status, 
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "false") boolean withReviseDate,
			@RequestParam("categoryIds") long[] categoryIds) {
		Long user_id = Long.parseLong(principal.getName());
		List<CommentForList> comments;
		if (withReviseDate == true) {
			comments = commentRepository.retrieveUserCommentsWithReviseDate(user_id, status, limit, offset, categoryIds);
		} else {			
			comments = commentRepository.retrieveUserComments(user_id, status, limit, offset, categoryIds);
		}
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("comments/count")
	public Integer retrieveCommentsCount(Principal principal, 
			@RequestParam(defaultValue = "added") String status,
			@RequestParam(defaultValue = "false") boolean withReviseDate,
			@RequestParam("categoryIds") long[] categoryIds) {
		Long user_id = Long.parseLong(principal.getName());
		if (withReviseDate == true) {
			return commentRepository.getUserCommentsWithReviseDateCount(user_id, status, categoryIds);
		} else {
			return commentRepository.getUserCommentsCount(user_id, status, categoryIds);
		}
	}

	@PostMapping("/comments")
	public Comment createComment(@RequestBody Comment comment, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);

		comment.setUser(userEntry.get());
		return commentRepository.save(comment);
	}
	
	@GetMapping("/comments/{id}")
	public Comment getComment(@PathVariable Long id, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Comment> commentEntry = commentRepository.findUserCommentById(user_id, id);
		if (commentEntry.isEmpty())
		 	throw new ResourceNotFoundException("comment id:" + id);

		return commentEntry.get();
	}

	@PutMapping("/comments/{id}")
	public Comment updateComment(@PathVariable Long id, @Valid @RequestBody CommentUpdateDto commentDto, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<Comment> commentEntry = commentRepository.findUserCommentById(user_id, id);
		if (commentEntry.isEmpty())
		 	throw new ResourceNotFoundException("comment id:" + id);

		commentEntry.get().setDescription(commentDto.description());
		commentEntry.get().setReviseDate(commentDto.reviseDate());
		return commentRepository.save(commentEntry.get());
	}

	@GetMapping("/project-categories/{category_id}/comments")
	public List<CommentForList> retrieveCategoryComments(Principal principal, @PathVariable Long category_id, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserProjectCategoryComments(user_id, category_id, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/project-categories/{category_id}/comments/count")
	public Integer retrieveCategoryCommentCount(@PathVariable Long category_id, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return commentRepository.getUserProjectCategoryCommentsCount(user_id, category_id, status);
	}

	@PostMapping("/project-categories/{category_id}/comments")
	public Comment createCategoryComment(@PathVariable Long category_id, @RequestBody Comment comment, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(user_id, category_id);
		if (categoryEntry.isEmpty())
		 	throw new ResourceNotFoundException("category id:" + category_id);
		log.debug("found project category: {}", categoryEntry);

		comment.setUser(userEntry.get());
		comment.setProjectCategory(categoryEntry.get());
		
		// TODO: return category name in the response
		return commentRepository.save(comment);
	}

	@GetMapping("/projects/{project_id}/comments")
	public List<CommentForList> retrieveProjectComments(Principal principal, @PathVariable Long project_id, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserProjectComments(user_id, project_id, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/projects/{project_id}/comments/count")
	public Integer retrieveProjectCommentsCount(@PathVariable Long project_id, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return commentRepository.getUserProjectCommentsCount(user_id, project_id, status);
	}

	@PostMapping("/projects/{project_id}/comments")
	public Comment createProjectComment(@PathVariable Long project_id, @RequestBody Comment comment, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(user_id, project_id);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + project_id);
		log.debug("found project: {}, project category: {}", projectEntry, projectEntry.get().getProjectCategory());

		comment.setUser(userEntry.get());
		comment.setProject(projectEntry.get());
		comment.setProjectCategory(projectEntry.get().getProjectCategory());
		return commentRepository.save(comment);
	}

	@GetMapping("/tasks/{task_id}/comments")
	public List<CommentForList> retrieveTaskComments(Principal principal, @PathVariable Long task_id, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserTaskComments(user_id, task_id, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/tasks/{task_id}/comments/count")
	public Integer retrieveTaskCommentsCount(@PathVariable Long task_id, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return commentRepository.getUserTaskCommentsCount(user_id, task_id, status);
	}

	@PostMapping("/tasks/{task_id}/comments")
	public Comment createTaskComment(@PathVariable Long task_id, @RequestBody Comment comment, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(user_id, task_id);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + task_id);
		log.debug("found task: {}, project: {}", taskEntry, taskEntry.get().getProject());

		comment.setUser(userEntry.get());
		comment.setTask(taskEntry.get());
		comment.setProject(taskEntry.get().getProject());
		comment.setProjectCategory(taskEntry.get().getProject().getProjectCategory());
		return commentRepository.save(comment);
	}

	@GetMapping("/pomodoros/{pomodoro_id}/comments")
	public List<CommentForList> retrievePomodoroComments(Principal principal, @PathVariable Long pomodoro_id, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long user_id = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserPomodoroComments(user_id, pomodoro_id, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/pomodoros/{pomodoro_id}/comments/count")
	public Integer retrievePomodoroCommentsCount(@PathVariable Long pomodoro_id, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return commentRepository.getUserPomodoroCommentsCount(user_id, pomodoro_id, status);
	}

	@PostMapping("/pomodoros/{pomodoro_id}/comments")
	public Comment createPomodoroComment(@PathVariable Long pomodoro_id, @RequestBody Comment comment, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(user_id);
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(user_id, pomodoro_id);
		if (pomodoroEntry.isEmpty())
		 	throw new ResourceNotFoundException("pomodoro id:" + pomodoro_id);
		log.debug("found pomodoro: {}, task: {}", pomodoroEntry, pomodoroEntry.get().getTask());

		comment.setUser(userEntry.get());
		comment.setPomodoro(pomodoroEntry.get());
		comment.setTask(pomodoroEntry.get().getTask());
		comment.setProject(pomodoroEntry.get().getTask().getProject());
		comment.setProjectCategory(pomodoroEntry.get().getTask().getProject().getProjectCategory());
		return commentRepository.save(comment);
	}
}

record CommentUpdateDto(@NotBlank String description, Instant reviseDate) {}
