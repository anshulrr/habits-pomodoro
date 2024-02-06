package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Comment;
import com.anshul.atomichabits.model.Pomodoro;
import com.anshul.atomichabits.model.Project;
import com.anshul.atomichabits.model.ProjectCategory;
import com.anshul.atomichabits.model.Tag;
import com.anshul.atomichabits.model.Task;
import com.anshul.atomichabits.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RestController
public class CommentResource {

	private UserRepository userRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private ProjectRepository projectRepository;
	private TaskRepository taskRepository;
	private PomodoroRepository pomodoroRepository;
	private CommentRepository commentRepository;
	private TagRepository tagRepository;

	@GetMapping("/comments")
	public List<CommentForList> retrieveComments(Principal principal, 
			@RequestParam(defaultValue = "added") String status, 
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "false") boolean filterWithReviseDate,
			@RequestParam(defaultValue = "") String searchString,
			@RequestParam("categoryIds") long[] categoryIds) {
		Long userId = Long.parseLong(principal.getName());
		List<CommentForList> comments;
		if (searchString.length() != 0 ) {
			comments = commentRepository.retrieveUserSearchedComments(userId, status, limit, offset, categoryIds, searchString);
		} else if (filterWithReviseDate) {
			comments = commentRepository.retrieveUserCommentsWithReviseDate(userId, status, limit, offset, categoryIds);
		} else {			
			comments = commentRepository.retrieveUserComments(userId, status, limit, offset, categoryIds);
		}
		log.trace("{}", comments);
		return comments;
	}

	@GetMapping("comments/count")
	public Integer retrieveCommentsCount(Principal principal, 
			@RequestParam(defaultValue = "added") String status,
			@RequestParam(defaultValue = "false") boolean filterWithReviseDate,
			@RequestParam(defaultValue = "") String searchString,
			@RequestParam("categoryIds") long[] categoryIds) {
		Long userId = Long.parseLong(principal.getName());
		if (searchString.length() != 0 ) {
			return commentRepository.getUserSearchedCommentsCount(userId, status, categoryIds, searchString);
		} else if (filterWithReviseDate) {
			return commentRepository.getUserCommentsWithReviseDateCount(userId, status, categoryIds);
		} else {
			return commentRepository.getUserCommentsCount(userId, status, categoryIds);
		}
	}

	@PostMapping("/comments")
	public Comment createComment(@RequestBody Comment comment, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(userId);
		if (userEntry.isEmpty())
			throw new ResourceNotFoundException("user id:" + userId);

		comment.setUser(userEntry.get());
		return commentRepository.save(comment);
	}
	
	@GetMapping("/comments/{id}")
	public Comment getComment(@PathVariable Long id, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<Comment> commentEntry = commentRepository.findUserCommentById(userId, id);
		if (commentEntry.isEmpty())
		 	throw new ResourceNotFoundException("cid:" + id);

		return commentEntry.get();
	}

	@PutMapping("/comments/{id}")
	public Comment updateComment(@PathVariable Long id, @Valid @RequestBody CommentUpdateDto commentDto, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<Comment> commentEntry = commentRepository.findUserCommentById(userId, id);
		if (commentEntry.isEmpty())
		 	throw new ResourceNotFoundException("comment id:" + id);

		commentEntry.get().setDescription(commentDto.description());
		commentEntry.get().setReviseDate(commentDto.reviseDate());
		return commentRepository.save(commentEntry.get());
	}

	@GetMapping("/project-categories/{categoryId}/comments")
	public List<CommentForList> retrieveCategoryComments(Principal principal, @PathVariable Long categoryId, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserProjectCategoryComments(userId, categoryId, status, limit, offset);
		log.trace("{}", comments);
		return comments;
	}

	@GetMapping("/project-categories/{categoryId}/comments/count")
	public Integer retrieveCategoryCommentCount(@PathVariable Long categoryId, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return commentRepository.getUserProjectCategoryCommentsCount(userId, categoryId, status);
	}

	@PostMapping("/project-categories/{categoryId}/comments")
	public Comment createCategoryComment(@PathVariable Long categoryId, @RequestBody Comment comment, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<ProjectCategory> categoryEntry = projectCategoryRepository.findUserProjectCategoryById(userId, categoryId);
		if (categoryEntry.isEmpty())
		 	throw new ResourceNotFoundException("category id:" + categoryId);
		log.debug("found project category: {}", categoryEntry);

		comment.setUser(userEntry.get());
		comment.setProjectCategory(categoryEntry.get());
		
		return commentRepository.save(comment);
	}

	@GetMapping("/projects/{projectId}/comments")
	public List<CommentForList> retrieveProjectComments(Principal principal, @PathVariable Long projectId, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserProjectComments(userId, projectId, status, limit, offset);
		log.trace("{}", comments);
		return comments;
	}

	@GetMapping("/projects/{projectId}/comments/count")
	public Integer retrieveProjectCommentsCount(@PathVariable Long projectId, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return commentRepository.getUserProjectCommentsCount(userId, projectId, status);
	}

	@PostMapping("/projects/{projectId}/comments")
	public Comment createProjectComment(@PathVariable Long projectId, @RequestBody Comment comment, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Project> projectEntry = projectRepository.findUserProjectById(userId, projectId);
		if (projectEntry.isEmpty())
		 	throw new ResourceNotFoundException("project id:" + projectId);
		log.debug("found project: {}, project category: {}", projectEntry, projectEntry.get().getProjectCategory());

		comment.setUser(userEntry.get());
		comment.setProject(projectEntry.get());
		comment.setProjectCategory(projectEntry.get().getProjectCategory());
		return commentRepository.save(comment);
	}

	@GetMapping("/tasks/{taskId}/comments")
	public List<CommentForList> retrieveTaskComments(Principal principal, @PathVariable Long taskId, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserTaskComments(userId, taskId, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/tasks/{taskId}/comments/count")
	public Integer retrieveTaskCommentsCount(@PathVariable Long taskId, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return commentRepository.getUserTaskCommentsCount(userId, taskId, status);
	}

	@PostMapping("/tasks/{taskId}/comments")
	public Comment createTaskComment(@PathVariable Long taskId, @RequestBody Comment comment, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Task> taskEntry = taskRepository.findUserTaskById(userId, taskId);
		if (taskEntry.isEmpty())
		 	throw new ResourceNotFoundException("task id:" + taskId);
		log.debug("found task: {}, project: {}", taskEntry, taskEntry.get().getProject());

		comment.setUser(userEntry.get());
		comment.setTask(taskEntry.get());
		comment.setProject(taskEntry.get().getProject());
		comment.setProjectCategory(taskEntry.get().getProject().getProjectCategory());
		return commentRepository.save(comment);
	}

	@GetMapping("/pomodoros/{pomodoroId}/comments")
	public List<CommentForList> retrievePomodoroComments(Principal principal, @PathVariable Long pomodoroId, @RequestParam(defaultValue = "added") String status, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		List<CommentForList> comments = commentRepository.retrieveUserPomodoroComments(userId, pomodoroId, status, limit, offset);
		log.trace("comments: {}", comments);
		return comments;
	}

	@GetMapping("/pomodoros/{pomodoroId}/comments/count")
	public Integer retrievePomodoroCommentsCount(@PathVariable Long pomodoroId, @RequestParam(defaultValue = "added") String status, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return commentRepository.getUserPomodoroCommentsCount(userId, pomodoroId, status);
	}

	@PostMapping("/pomodoros/{pomodoroId}/comments")
	public Comment createPomodoroComment(@PathVariable Long pomodoroId, @RequestBody Comment comment, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		Optional<User> userEntry = userRepository.findById(userId);
		Optional<Pomodoro> pomodoroEntry = pomodoroRepository.findUserPomodoroById(userId, pomodoroId);
		if (pomodoroEntry.isEmpty())
		 	throw new ResourceNotFoundException("pomodoro id:" + pomodoroId);
		log.debug("found pomodoro: {}, task: {}", pomodoroEntry, pomodoroEntry.get().getTask());

		comment.setUser(userEntry.get());
		comment.setPomodoro(pomodoroEntry.get());
		comment.setTask(pomodoroEntry.get().getTask());
		comment.setProject(pomodoroEntry.get().getTask().getProject());
		comment.setProjectCategory(pomodoroEntry.get().getTask().getProject().getProjectCategory());
		return commentRepository.save(comment);
	}

	@PostMapping("/comments/{id}/tags")
	public ResponseEntity<Comment> addTag(Principal principal, 
			@PathVariable Long id, 
			@RequestBody MapTagsRequest request) {
		Long userId = Long.parseLong(principal.getName());
		Optional<Comment> commentEntry = commentRepository.findUserCommentById(userId, id);
		if (commentEntry.isEmpty())
		 	throw new ResourceNotFoundException("comment id:" + id);
		
		Set<Tag> tags = tagRepository.findUserTagByIds(userId, request.tagIds());
		
		commentEntry.get().setTags(tags);
		
	    return new ResponseEntity<>(commentRepository.save(commentEntry.get()), HttpStatus.CREATED);
	}
	
	@GetMapping("/comments/tags")
	public List<Object> retrieveCommentsTags(Principal principal, 
			@RequestParam("commentIds") long[] commentIds) {
		return commentRepository.findCommentsTagsByIds(commentIds);
	}
}

record CommentUpdateDto(@NotBlank String description, Instant reviseDate) {}
