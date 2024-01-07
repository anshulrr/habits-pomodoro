package com.anshul.atomichabits.jpa;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.dto.TaskForNotifications;
import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = :user_id and t.id = :id")
	public Optional<Task> findUserTaskById(Long user_id, Long id);

	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr project
			from tasks t
			join projects pr on t.project.id = pr.id
			where t.user.id = :user_id and t.project.id = :project_id and t.status = :status
			group by t.id, pr.id
			order by t.priority asc, t.id desc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and project_id = :project_id and status = :status", nativeQuery = true)
	public Integer getProjectTasksCount(Long user_id, Long project_id, String status);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr project
			from tasks t
			join projects pr on t.project.id = pr.id
			where t.user.id = :user_id and t.status = :status and dueDate >= :start and dueDate <= :end
			group by t.id, pr.id
			order by t.dueDate asc, t.priority asc, t.id asc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveFilteredTasks(Long user_id, String status, Instant start, Instant end, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and status = :status and due_date >= :start and due_date <= :end", nativeQuery = true)
	public Integer getFilteredTasksCount(Long user_id, String status, Instant start, Instant end);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.type type, t.dueDate dueDate, t.repeatDays repeatDays, t.dailyLimit dailyLimit, t.enableNotifications enableNotifications, t.pomodoroLength pomodoroLength, 
			pr project
			from tasks t
			join projects pr on t.project.id = pr.id
			join t.tags tags
			where t.user.id = :user_id and t.status = :status and tags.id = :tagId
			group by t.id, pr.id, pr.projectCategory.level
			order by pr.projectCategory.level, pr.priority, t.priority asc, t.id asc
			limit :limit offset :offset
			""")
	public List<TaskForList> findTasksByUserIdAndTagsId(Long user_id, Long tagId, String status, int limit, int offset);
	
	@Query(value = """
				select count(*) 
				from tasks t
				join t.tags tags 
				where t.user.id = :user_id and tags.id = :tag_id and status = :status
			""")
	public Integer getTagsTasksCount(Long user_id, Long tag_id, String status);
	
	@Query(value = "select * from tasks_tags t where t.task_id in :ids", nativeQuery = true)
	public List<Object> findTaskTagsByIds(long[] ids);
	
	@Query("""
			select p.task.id taskId, sum(p.timeElapsed) timeElapsed
			from pomodoros p
			where p.user.id = :user_id and p.endTime >= :start and p.endTime <= :end and p.status in ('completed', 'past') and p.task.id in :ids
			group by p.task.id
			""")
	public List<Object> findTasksTimeElapsed(Long user_id, OffsetDateTime start, OffsetDateTime end, long[] ids);
	
	@Query(value = """
			select t.id, t.description description, t.type type, t.dueDate dueDate, t.user.email email
			from tasks t
			where t.dueDate > :start and t.dueDate <= :end and t.type in ('good', 'neutral') and t.enableNotifications = TRUE
			""")
	public List<TaskForNotifications> getNotificationTasks(Instant start, Instant end);
}
