package com.anshul.atomichabits.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import com.anshul.atomichabits.dto.CommentForList;
import com.anshul.atomichabits.dto.CommentForSync;
import com.anshul.atomichabits.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
	
	@Query("select c from comments c where c.user.id = :userId and c.id = :id")
	public Optional<Comment> findUserCommentById(Long userId, UUID id);

	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task  
			from comments c
			left join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.status = :status
			and (c.project_category_id in :categoryIds or c.project_category_id is null)
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserComments(Long userId, String status, int limit, int offset, long[] categoryIds);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.updated_at updatedAt, c.revise_date reviseDate, 
			pc.id categoryId, p.id projectId, t.id taskId,
			pc.name category, p.name project, p.color color, t.description task
			from comments c
			left join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.status = :status and c.updated_at > :lastSyncTime
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForSync> retrieveUserSyncComments(Long userId, String status, int limit, int offset, Instant lastSyncTime);
	
	@Query(value = """
			select count(*) from comments 
			where user_id = :userId and status = :status 
			and (project_category_id is null or project_category_id in :categoryIds) 
			""", nativeQuery = true)
	public Integer getUserCommentsCount(Long userId, String status, UUID[] categoryIds);
	
	@Query(value = """
			select count(*) from comments 
			where user_id = :userId and status = :status 
			""", nativeQuery = true)
	public Integer getUserSyncCommentsCount(Long userId, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task  
			from comments c
			left join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.status = :status and c.revise_date is not null
			and (c.project_category_id in :categoryIds or c.project_category_id is null)
			order by c.revise_date
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserCommentsWithReviseDate(Long userId, String status, int limit, int offset, long[] categoryIds);
	
	@Query(value = """
			select count(*) from comments 
			where user_id = :userId and status = :status and revise_date is not null
			and (project_category_id is null or project_category_id in :categoryIds) 
			""", nativeQuery = true)
	public Integer getUserCommentsWithReviseDateCount(Long userId, String status, UUID[] categoryIds);
	
	// TODO: optimize query for with 'or' for task description match
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task  
			from comments c
			left join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.status = :status 
			and (websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', c.description)
			    or websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', t.description))
			and (c.project_category_id in :categoryIds or c.project_category_id is null)
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserSearchedComments(Long userId, String status, int limit, int offset, long[] categoryIds, String searchString);
	
	@Query(value = """
			select count(*) from comments c
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.status = :status 
			and (websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', c.description)
			    or websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', t.description))
			and (c.project_category_id is null or c.project_category_id in :categoryIds) 
			""", nativeQuery = true)
	public Integer getUserSearchedCommentsCount(Long userId, String status, UUID[] categoryIds, String searchString);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			left join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.project_category_id = :categoryId and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserProjectCategoryComments(Long userId, UUID categoryId, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :userId and project_category_id = :categoryId and status = :status", nativeQuery = true)
	public Integer getUserProjectCategoryCommentsCount(Long userId, UUID categoryId, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			left join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.project_id = :projectId and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserProjectComments(Long userId, UUID projectId, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :userId and project_id = :projectId and status = :status", nativeQuery = true)
	public Integer getUserProjectCommentsCount(Long userId, UUID projectId, String status);
	
	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.task_id = :taskId and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserTaskComments(Long userId, UUID taskId, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :userId and task_id = :taskId and status = :status", nativeQuery = true)
	public Integer getUserTaskCommentsCount(Long userId, UUID taskId, String status);

	@Query(value = """
			select c.*, c.created_at createdAt, c.revise_date reviseDate, pc.name category, p.name project, p.color color, t.description task 
			from comments c
			join project_categories pc on c.project_category_id = pc.id
			join projects p on c.project_id = p.id
			join tasks t on c.task_id = t.id
			where c.user_id = :userId and c.pomodoro_id = :pomodoroId and c.status = :status
			order by c.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<CommentForList> retrieveUserPomodoroComments(Long userId, UUID pomodoroId, String status, int limit, int offset);
	
	@Query(value = "select count(*) from comments where user_id = :userId and pomodoro_id = :pomodoroId and status = :status", nativeQuery = true)
	public Integer getUserPomodoroCommentsCount(Long userId, UUID pomodoroId, String status);
	
	@Modifying
	@Query(value = "update comments set project_category_id = :categoryId where user_id = :userId and project_id = :projectId", nativeQuery = true)
	public void updateCommentsCategory(Long userId, UUID projectId, UUID categoryId);
	
	@Modifying
	@Query(value = "update comments set project_id = :projectId, project_category_id = :categoryId where user_id = :userId and task_id = :taskId", nativeQuery = true)
	public void updateCommentsProjectAndCategory(Long userId, UUID taskId, UUID projectId, UUID categoryId);
	
	@Query(value = "select * from comments_tags t where t.comment_id in :ids", nativeQuery = true)
	public List<Object> findCommentsTagsByIds(UUID[] ids);
}
