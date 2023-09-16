package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.CommentForList;
import com.anshul.atomichabits.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	@Query("select c from comments c where c.user.id = :user_id and c.id = :id")
	public Optional<Comment> findUserCommentById(Long user_id, Long id);

	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task  
			from comments c
			left join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :user_id and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserComments(Long user_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :user_id and status = :status", nativeQuery = true)
	public Integer getUserCommentsCount(Long user_id, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :user_id and c.project_category_id = :project_category_id and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserProjectCategoryComments(Long user_id, Long project_category_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :user_id and project_category_id = :project_category_id and status = :status", nativeQuery = true)
	public Integer getUserProjectCategoryCommentsCount(Long user_id, Long project_category_id, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :user_id and c.project_id = :project_id and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserProjectComments(Long user_id, Long project_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :user_id and project_id = :project_id and status = :status", nativeQuery = true)
	public Integer getUserProjectCommentsCount(Long user_id, Long project_id, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			join tasks t on c.task_id = t.id
			where c.user_id = :user_id and c.task_id = :task_id and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserTaskComments(Long user_id, Long task_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :user_id and task_id = :task_id and status = :status", nativeQuery = true)
	public Integer getUserTaskCommentsCount(Long user_id, Long task_id, String status);

	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			join tasks t on c.task_id = t.id
			where c.user_id = :user_id and c.pomodoro_id = :pomodoro_id and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserPomodoroComments(Long user_id, Long pomodoro_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :user_id and pomodoro_id = :pomodoro_id and status = :status", nativeQuery = true)
	public Integer getUserPomodoroCommentsCount(Long user_id, Long pomodoro_id, String status);
}
