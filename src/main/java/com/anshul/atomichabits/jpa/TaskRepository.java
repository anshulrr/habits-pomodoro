package com.anshul.atomichabits.jpa;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.dto.TaskForNotifications;
import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = :userId and t.id = :id")
	public Optional<Task> findUserTaskById(Long userId, Long id);

	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr.id projectId
			from tasks t
			join projects pr on t.project.id = pr.id
			where t.user.id = :userId and t.project.id = :projectId and t.status = :status
			group by t.id, pr.id
			order by t.priority asc, t.id desc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveUserTasksByProjectId(Long userId, Long projectId, String status, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :userId and project_id = :projectId and status = :status", nativeQuery = true)
	public Integer getProjectTasksCount(Long userId, Long projectId, String status);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr.id projectId
			from tasks t
			join projects pr on t.project.id = pr.id
			where t.user.id = :userId and t.status = :status and dueDate >= :start and dueDate <= :end
			group by t.id, pr.id
			order by t.dueDate asc, t.priority asc, t.id asc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveFilteredTasks(Long userId, String status, Instant start, Instant end, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :userId and status = :status and due_date >= :start and due_date <= :end", nativeQuery = true)
	public Integer getFilteredTasksCount(Long userId, String status, Instant start, Instant end);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr.id projectId
			from tasks t
			join projects pr on t.project.id = pr.id
			join t.tags tags
			where t.user.id = :userId and t.status = :status and tags.id = :tagId
			group by t.id, pr.id, pr.projectCategory.level
			order by pr.projectCategory.level, pr.priority, t.priority asc, t.id asc
			limit :limit offset :offset
			""")
	public List<TaskForList> findTasksByUserIdAndTagsId(Long userId, Long tagId, String status, int limit, int offset);
	
	@Query(value = """
			select count(*) 
			from tasks t
			join t.tags tags 
			where t.user.id = :userId and tags.id = :tagId and status = :status
			""")
	public Integer getTagsTasksCount(Long userId, Long tagId, String status);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.due_date dueDate, t.repeat_days repeatDays, t.daily_limit dailyLimit, t.enable_notifications enableNotifications, t.pomodoro_length pomodoroLength, 
			t.project_id projectId
			from tasks t
			join projects as pr on t.project_id = pr.id
			join project_categories as pc on pr.project_category_id = pc.id
			where t.user_id = :userId and t.status = :status and websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', t.description)
			group by t.id, pr.id, pc.id
			order by pc.level, pr.priority, t.priority asc, t.id asc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<TaskForList> retrieveSearchedTasks(Long userId, String status, String searchString, int limit, int offset);
	
	@Query(value = """
			select count(*) 
			from tasks 
			where user_id = :userId and status = :status and websearch_to_tsquery('english', :searchString)  @@ to_tsvector('english', description)
			""", nativeQuery = true)
	public Integer getSearchedTasksCount(Long userId, String status, String searchString);
	
	@Query(value = "select * from tasks_tags t where t.task_id in :ids", nativeQuery = true)
	public List<Object> findTaskTagsByIds(long[] ids);
	
	@Query(value = """
			select task_id, count(*) commentsCount
			from comments
			where task_id in :ids
			group by task_id
			""", nativeQuery = true)
	public List<Object> countTaskCommentsByIds(long[] ids);
	
	@Query("""
			select p.task.id taskId, sum(p.timeElapsed) timeElapsed
			from pomodoros p
			where p.user.id = :userId and p.endTime >= :start and p.endTime <= :end and p.status in ('completed', 'past') and p.task.id in :ids
			group by p.task.id
			""")
	public List<Object> findTasksTimeElapsed(Long userId, OffsetDateTime start, OffsetDateTime end, long[] ids);
	
	@Query(value = """
			select t.id, t.description description, t.type type, t.dueDate dueDate, t.user.email email
			from tasks t
			where t.dueDate > :start and t.dueDate <= :end and t.type in ('good', 'neutral') and t.enableNotifications = TRUE
			""")
	public List<TaskForNotifications> getNotificationTasks(Instant start, Instant end);
	
	@Modifying
	@Query(value = """
			WITH reordered_items AS (
			SELECT id, ROW_NUMBER() OVER (ORDER BY priority, id desc) * 1000 AS new_order
			FROM tasks
			WHERE user_id = :userId and project_id = :projectId
			)
			UPDATE tasks
			SET priority = reordered_items.new_order
			FROM reordered_items
			WHERE tasks.id = reordered_items.id
			""", nativeQuery = true)
	public void updateTasksPriorityOrder(Long userId, Long projectId);
}
