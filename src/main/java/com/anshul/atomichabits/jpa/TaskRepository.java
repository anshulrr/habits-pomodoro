package com.anshul.atomichabits.jpa;

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
			select t.*, t.due_date dueDate, t.pomodoro_length pomodoroLength, sum(p.time_elapsed) pomodorosTimeElapsed
			from tasks t
			left join pomodoros p on t.id = p.task_id and p.status in ('completed', 'past') 
			where t.user_id = :user_id and t.project_id = :project_id and t.status = :status
			group by t.id
			order by t.priority asc, t.id desc
			limit :limit offset :offset
			""", nativeQuery = true)
	public List<TaskForList> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status, int limit, int offset);
	
	@Query(value = "select count(*) from tasks where user_id = :user_id and project_id = :project_id and status = :status", nativeQuery = true)
	public Integer getProjectTasksCount(Long user_id, Long project_id, String status);
}
