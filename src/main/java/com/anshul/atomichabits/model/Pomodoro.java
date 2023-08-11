package com.anshul.atomichabits.model;

import java.time.OffsetDateTime;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "pomodoros")
@Table(indexes= {
		@Index(name="pomodoros_end_time_index", columnList="endTime"),
		@Index(name="pomodoros_status_index", columnList="status"),
		@Index(name="pomodoros_task_index", columnList="task_id"),
		@Index(name="pomodoros_user_index", columnList="user_id")
})
public class Pomodoro {

	@Id
	@GeneratedValue
	private Long id;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime startTime;
	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime endTime;

	// in minutes
	private Integer length = 25;
	// in seconds
	private Integer timeElapsed;

	// started, paused, completed, discarded
	private String status = "started";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Task task;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	@Override
	public String toString() {
		return "Pomodoro [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", length=" + length
				+ ", timeElapsed=" + timeElapsed + ", status=" + status + ", task=" + task.getDescription()
				+ ", useremail=" + user.getEmail() + "]";
	}

}
