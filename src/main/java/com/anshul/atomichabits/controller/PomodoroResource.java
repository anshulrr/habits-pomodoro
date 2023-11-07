package com.anshul.atomichabits.controller;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anshul.atomichabits.business.PomodoroService;
import com.anshul.atomichabits.business.StatsService;
import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.dto.PomodoroUpdateDto;
import com.anshul.atomichabits.model.Pomodoro;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PomodoroResource {

	private PomodoroService pomodoroService;
	private StatsService statsService;
	
	@GetMapping("/pomodoros")
	public List<PomodoroForList> retrievePomodorosOfUser(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return pomodoroService.retrievePomodoros(user_id, startDate, endDate, categories);
		} else {
			return pomodoroService.retrievePomodoros(subjectId, startDate, endDate, categories);
		}
	}

	@GetMapping("/stats/projects-time")
	public List<Object> retrieveProjectPomodoros(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return statsService.retrieveProjectPomodoros(user_id, startDate, endDate, categories);
		} else {
			// TODO: check if subject is mapped
			return statsService.retrieveProjectPomodoros(subjectId, startDate, endDate, categories);
		}
	}

	@GetMapping("/stats/tasks-time")
	public List<Object> retrieveTaskPomodoros(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId == null) {
			return statsService.retrieveTaskPomodoros(user_id, startDate, endDate, categories);
		} else {
			return statsService.retrieveTaskPomodoros(subjectId, startDate, endDate, categories);
		}
	}

	@GetMapping("/stats/total-time")
	public Map<String, List<String[]>> retrieveTotalPomodoros(Principal principal,
			@RequestParam(required = false) Long subjectId,
			@RequestParam String limit,
			@RequestParam OffsetDateTime startDate, 
			@RequestParam OffsetDateTime endDate,
			@RequestParam("include_categories") long[] categories,
			@RequestParam(defaultValue = "UTC") String timezone) {
		Long user_id = Long.parseLong(principal.getName());
		if (subjectId == null) {
			return statsService.retrieveTotalPomodoros(user_id, limit, startDate, endDate, categories, timezone);
		} else {
			return statsService.retrieveTotalPomodoros(subjectId, limit, startDate, endDate, categories, timezone);
		}
	}

	@PostMapping("/pomodoros")
	public ResponseEntity<Pomodoro> createPomodoro(Principal principal, 
			@RequestParam Long task_id,
			@Valid @RequestBody Pomodoro pomodoro) {	
		Long user_id = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.createPomodoro(user_id, task_id, pomodoro), HttpStatus.OK);
	}

	@PostMapping("/pomodoros/past")
	public ResponseEntity<Pomodoro> createPastPomodoro(Principal principal, 
			@RequestParam Long task_id,
			@Valid @RequestBody Pomodoro pomodoro) {	
		Long user_id = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.createPastPomodoro(user_id, task_id, pomodoro), HttpStatus.OK);
	}
	
	@DeleteMapping("/pomodoros/past/{id}")
	public ResponseEntity<Pomodoro> deletePastPomodoro(Principal principal, @PathVariable Long id) {
		Long user_id = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.deletePastPomodoro(user_id, id), HttpStatus.OK);
	}

	@PutMapping("/pomodoros/{id}")
	public ResponseEntity<Pomodoro> updatePomodoro(@PathVariable Long id,
			@RequestBody PomodoroUpdateDto pomodoroUpdateDto, Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.updatePomodoro(user_id, id, pomodoroUpdateDto), HttpStatus.OK);
	}
	
	@GetMapping("/pomodoros/running")
	public ResponseEntity<PomodoroDto> getRunningPomodoro(Principal principal) {
		Long user_id = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.getRunningPomodoro(user_id), HttpStatus.OK);
	}
}