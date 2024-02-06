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

import com.anshul.atomichabits.business.AccountabilityPartnerService;
import com.anshul.atomichabits.business.PomodoroService;
import com.anshul.atomichabits.business.StatsService;
import com.anshul.atomichabits.dto.TotalChartProjectData;
import com.anshul.atomichabits.dto.PomodoroDto;
import com.anshul.atomichabits.dto.PomodoroForList;
import com.anshul.atomichabits.dto.PomodoroUpdateDto;
import com.anshul.atomichabits.model.Pomodoro;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
public class PomodoroResource {

	private PomodoroService pomodoroService;
	private StatsService statsService;
	private AccountabilityPartnerService accountabilityPartnerService;
	
	private static final String LOG_MESSAGE = "{} tried unauthorized access of {} stats";
	
	@GetMapping("/pomodoros")
	public List<PomodoroForList> retrievePomodorosOfUser(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return pomodoroService.retrievePomodoros(userId, startDate, endDate, categories);
		} else {
			log.info(LOG_MESSAGE, userId, subjectId);
			return pomodoroService.retrievePomodoros(subjectId, startDate, endDate, categories);
		}
	}
	
	@GetMapping("/stats/project-categories-time")
	public ResponseEntity<List<Object>> retrieveProjectCategoriesPomodoros(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return new ResponseEntity<>(statsService.retrieveProjectCategoriesPomodoros(userId, startDate, endDate, categories), HttpStatus.OK);
		} else {
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {	
				return new ResponseEntity<>(statsService.retrieveProjectCategoriesPomodoros(subjectId, startDate, endDate, categories), HttpStatus.OK);
			} else {
				log.info(LOG_MESSAGE, userId, subjectId);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@GetMapping("/stats/projects-time")
	public ResponseEntity<List<Object>> retrieveProjectPomodoros(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return new ResponseEntity<>(statsService.retrieveProjectPomodoros(userId, startDate, endDate, categories), HttpStatus.OK);
		} else {
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {	
				return new ResponseEntity<>(statsService.retrieveProjectPomodoros(subjectId, startDate, endDate, categories), HttpStatus.OK);
			} else {
				log.info(LOG_MESSAGE, userId, subjectId);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@GetMapping("/stats/tasks-time")
	public ResponseEntity<List<Object>> retrieveTaskPomodoros(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam("include_categories") long[] categories) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {
			return new ResponseEntity<>(statsService.retrieveTaskPomodoros(userId, startDate, endDate, categories), HttpStatus.OK);
		} else {
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {	
				return new ResponseEntity<>(statsService.retrieveTaskPomodoros(subjectId, startDate, endDate, categories), HttpStatus.OK);
			} else {
				log.info(LOG_MESSAGE, userId, subjectId);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@GetMapping("/stats/total-time")
	public ResponseEntity<Map<String, TotalChartProjectData>> retrieveTotalPomodoros(Principal principal,
			@RequestParam(required = false) Long subjectId,
			@RequestParam String limit,
			@RequestParam OffsetDateTime startDate, 
			@RequestParam OffsetDateTime endDate,
			@RequestParam("include_categories") long[] categories,
			@RequestParam(defaultValue = "UTC") String timezone) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {
			return new ResponseEntity<>(statsService.retrieveTotalPomodoros(userId, limit, startDate, endDate, categories, timezone), HttpStatus.OK);
		} else {
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {	
				return new ResponseEntity<>(statsService.retrieveTotalPomodoros(subjectId, limit, startDate, endDate, categories, timezone), HttpStatus.OK);
			} else {
				log.info(LOG_MESSAGE, userId, subjectId);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}
	
	@GetMapping("/stats/pomodoros-count")
	public ResponseEntity<List<Object>> retrieveProjectCategoriesPomodorosCount(Principal principal, 
			@RequestParam(required = false) Long subjectId,
			@RequestParam OffsetDateTime startDate,
			@RequestParam OffsetDateTime endDate, 
			@RequestParam(defaultValue = "user")  String type,
			@RequestParam(name = "include_categories", required = false) long[] categories,
			@RequestParam(required = false) Long typeId,
			@RequestParam(defaultValue = "UTC") String timezone) {
		Long userId = Long.parseLong(principal.getName());
		if (subjectId == null) {	
			return new ResponseEntity<>(statsService.retrievePomodorosCount(userId, startDate, endDate, type, typeId, categories, timezone), HttpStatus.OK);
		} else {
			if (accountabilityPartnerService.isSubject(userId, subjectId)) {	
				return new ResponseEntity<>(statsService.retrievePomodorosCount(subjectId, startDate, endDate, type, typeId, categories, timezone), HttpStatus.OK);
			} else {
				log.info(LOG_MESSAGE, userId, subjectId);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}
	
	@GetMapping("/stats/task-pomodoros")
	public List<Object> retrieveTaskPomodorosList(Principal principal, 
			@RequestParam Long taskId,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "0") int offset) {
		Long userId = Long.parseLong(principal.getName());
		return statsService.retrieveTaskPomodoros(userId, taskId, limit, offset);
	}

	@GetMapping("/stats/task-pomodoros/count")
	public Integer retrieveTaskPomodorosCount(Principal principal, @RequestParam Long taskId) {
		Long userId = Long.parseLong(principal.getName());
		return statsService.retrieveTaskPomodorosCount(userId, taskId);
	}

	@PostMapping("/pomodoros")
	public ResponseEntity<Pomodoro> createPomodoro(Principal principal, 
			@RequestParam Long taskId,
			@Valid @RequestBody Pomodoro pomodoro) {	
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.createPomodoro(userId, taskId, pomodoro), HttpStatus.OK);
	}

	@PostMapping("/pomodoros/past")
	public ResponseEntity<Pomodoro> createPastPomodoro(Principal principal, 
			@RequestParam Long taskId,
			@Valid @RequestBody Pomodoro pomodoro) {	
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.createPastPomodoro(userId, taskId, pomodoro), HttpStatus.OK);
	}
	
	@DeleteMapping("/pomodoros/{id}")
	public ResponseEntity<Pomodoro> deletePomodoro(Principal principal, @PathVariable Long id) {
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.deletePomodoro(userId, id), HttpStatus.OK);
	}

	@PutMapping("/pomodoros/{id}")
	public ResponseEntity<Pomodoro> updatePomodoro(@PathVariable Long id,
			@RequestBody PomodoroUpdateDto pomodoroUpdateDto, Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.updatePomodoro(userId, id, pomodoroUpdateDto), HttpStatus.OK);
	}
	
	@GetMapping("/pomodoros/running")
	public ResponseEntity<PomodoroDto> getRunningPomodoro(Principal principal) {
		Long userId = Long.parseLong(principal.getName());
		return new ResponseEntity<>(pomodoroService.getRunningPomodoro(userId), HttpStatus.OK);
	}
}