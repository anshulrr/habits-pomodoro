package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.TaskForList;
import com.anshul.atomichabits.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from tasks t where t.user.id = ?1 and t.id = ?2")
	public Optional<Task> findUserTaskById(Long user_id, Long id);

	@Query(value = """
			select t.*, t.pomodoro_length pomodoroLength, sum(p.time_elapsed) pomodorosTimeElapsed
			from tasks t
			left join pomodoros p on t.id = p.task_id
			where t.user_id = ?1 and t.project_id = ?2 and t.status = ?3 
			group by t.id
			order by t.id desc
			""", nativeQuery = true)
	public List<TaskForList> retrieveUserTasksByProjectId(Long user_id, Long project_id, String status);
}
