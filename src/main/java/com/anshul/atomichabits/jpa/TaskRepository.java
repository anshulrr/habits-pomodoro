package com.anshul.atomichabits.jpa;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = :user_id and t.id = :id")
	public Optional<Task> findUserTaskById(Long user_id, Long id);

//	@Query(value = """
//			select t.id id, t.description description, t.status status, t.pomodoroLength pomodoroLength, t.priority priority, t.dueDate dueDate,
//			t.project project
//			from tasks t
//			where t.user.id = :user_id and t.project.id = :project_id and t.status = :status
//			order by t.priority asc, t.id desc
//			limit :limit offset :offset
//			""")
	@Query(value = """
			select t.*, t.due_date dueDate, t.pomodoro_length pomodoroLength, pr.color color, pr.id projectId, pr.pomodoro_length projectPomodoroLength, sum(p.time_elapsed) pomodorosTimeElapsed, pr.name project
			from tasks t
			left join pomodoros p on t.id = p.task_id and p.status in ('completed', 'past') 
			join projects pr on pr.id = t.project_id 
			where t.user_id = :user_id and t.project_id = :project_id and t.status = :status
			group by t.id, pr.name, pr.color, pr.id, pr.pomodoro_length
			order by t.priority asc, t.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<TaskForList> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and project_id = :project_id and status = :status", nativeQuery = true)
	public Integer getProjectTasksCount(Long user_id, Long project_id, String status);
	
	@Query(value = """
			select t.*, t.due_date dueDate, t.pomodoro_length pomodoroLength, sum(p.time_elapsed) pomodorosTimeElapsed, pr.name project, pr.color color, pr.id projectId
			from tasks t
			left join pomodoros p on t.id = p.task_id and p.status in ('completed', 'past')
			join projects pr on pr.id = t.project_id 
			where t.user_id = :user_id and t.status = :status and due_date >= :start and due_date <= :end
			group by t.id, pr.name, pr.color, pr.id
			order by t.due_date desc, t.priority asc, t.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<TaskForList> retrieveFilteredTasks(Long user_id, String status, OffsetDateTime start, OffsetDateTime end, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and status = :status and due_date >= :start and due_date <= :end", nativeQuery = true)
	public Integer getFilteredTasksCount(Long user_id, String status, OffsetDateTime start, OffsetDateTime end);
}
