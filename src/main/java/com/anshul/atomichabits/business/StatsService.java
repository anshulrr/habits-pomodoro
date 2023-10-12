package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.PomodoroRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class StatsService {

	private PomodoroRepository pomodoroRepository;
	
	public List<Object> retrieveProjectPomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		List<Object> result = pomodoroRepository.findProjectsTime(user_id, startDate, endDate, categories);
		return result;
	}
	
	public List<Object> retrieveTaskPomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		List<Object> result = pomodoroRepository.findTasksTime(user_id, startDate, endDate, categories);
		return result;
	}
	
	public Map<String, List<String[]>> retrieveTotalPomodoros(Long user_id, String limit, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories, String timezone) {
		if (limit.equals("daily")) {
			limit = "DD";
		} else if (limit.equals("weekly")) {
			limit = "IW";
		} else if (limit.equals("monthly")) {
			limit = "MM";
		}
		
		List<String[]> result = pomodoroRepository.findTotalTime(user_id, startDate, endDate, categories, timezone,
				limit);
		log.trace("total time result: {}", result);

		Map<String, List<String[]>> groupedResult = result.stream()
				.collect(Collectors.groupingBy((element -> (String) element[2])));
		log.trace("total time groupedResult: {}", groupedResult);

		return groupedResult;
	}
}
