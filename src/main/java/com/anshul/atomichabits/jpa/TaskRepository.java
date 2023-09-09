package com.anshul.atomichabits.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = :user_id and t.id = :id")
	public Optional<Task> findUserTaskById(Long user_id, Long id);

	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.dueDate dueDate, t.pomodoroLength pomodoroLength, 
			sum(p.timeElapsed) pomodorosTimeElapsed, 
			pr project
			from tasks t
			left join t.pomodoros p 
			join projects pr on t.project.id = pr.id
			where t.user.id = :user_id and t.project.id = :project_id and t.status = :status
			and p.status in ('completed', 'past')
			group by t.id, pr.id
			order by t.priority asc, t.id desc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and project_id = :project_id and status = :status", nativeQuery = true)
	public Integer getProjectTasksCount(Long user_id, Long project_id, String status);
	
	@Query(value = """
			select t.id id, t.priority priority, t.description description, t.status status, t.dueDate dueDate, t.pomodoroLength pomodoroLength, 
			sum(p.timeElapsed) pomodorosTimeElapsed, 
			pr project
			from tasks t
			left join t.pomodoros p 
			join projects pr on t.project.id = pr.id
			where t.user.id = :user_id and t.status = :status and dueDate >= :start and dueDate <= :end
			and p.status in ('completed', 'past')
			group by t.id, pr.id
			order by t.dueDate desc, t.priority asc, t.id desc
			limit :limit offset :offset
			""")
	public List<TaskForList> retrieveFilteredTasks(Long user_id, String status, Instant start, Instant end, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and status = :status and due_date >= :start and due_date <= :end", nativeQuery = true)
	public Integer getFilteredTasksCount(Long user_id, String status, Instant start, Instant end);
}
