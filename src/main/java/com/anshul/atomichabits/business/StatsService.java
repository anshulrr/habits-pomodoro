package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.dto.IndexedTime;
import com.anshul.atomichabits.dto.TotalChartProjectData;
import com.anshul.atomichabits.jpa.PomodoroRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class StatsService {

	private PomodoroRepository pomodoroRepository;
	
	public List<Object> retrieveProjectCategoriesPomodoros(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		return pomodoroRepository.findProjectCategoriesTime(userId, startDate, endDate, categories);
	}
	
	public List<Object> retrieveProjectPomodoros(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		return pomodoroRepository.findProjectsTime(userId, startDate, endDate, categories);
	}
	
	public List<Object> retrieveTaskPomodoros(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		return pomodoroRepository.findTasksTime(userId, startDate, endDate, categories);
	}
	
	public Map<String, TotalChartProjectData> retrieveTotalPomodoros(String entity, Long userId, String limit, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories, String timezone) {
		// temporary fix for timezone issue with latest postgres / IANA timezone
		if (timezone.equals("Asia/Calcutta")) {
			timezone = "Asia/Kolkata";
		}
		if (limit.equals("daily")) {
			limit = "DD";
		} else if (limit.equals("weekly")) {
			limit = "IW";
		} else if (limit.equals("monthly")) {
			limit = "MM";
		} else if (limit.equals("yearly")) {
			limit = "YYYY";
		}
		
		List<String[]> result;
		if (entity.equals("task")) {
			result = pomodoroRepository.findTasksTotalTime(userId, startDate, endDate, categories, timezone, limit);
		} else {			
			result = pomodoroRepository.findTotalTime(userId, startDate, endDate, categories, timezone, limit);
		}
		log.trace("total time result: {}", result);

		Map<String, TotalChartProjectData> groupedResult = new HashMap<>();
		
		for (String[] strArr: result) {
			String project = strArr[2];
			if (groupedResult.containsKey(project)) {
				groupedResult.get(project).dataArr().add(new IndexedTime(Integer.valueOf(strArr[0]), Integer.valueOf(strArr[1])));
			} else {
				List<IndexedTime> dataArr = new ArrayList<>();
				dataArr.add(new IndexedTime(Integer.valueOf(strArr[0]), Integer.valueOf(strArr[1])));
				TotalChartProjectData obj = new TotalChartProjectData(dataArr, strArr[3], Integer.valueOf(strArr[4]), Integer.valueOf(strArr[5]));
				groupedResult.put(project, obj);
			}
		}

		return groupedResult;
	}
	
	public List<Object> retrievePomodorosCount(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, String type, Long typeId, long[] categories, String timezone) {
		// temporary fix for timezone issue with latest postgres / IANA timezone
		// TODO: find generic solution
		if (timezone.equals("Asia/Calcutta")) {
			timezone = "Asia/Kolkata";
		}
		List<Object> result = null;
		if (type.equals("user")) {
			result = pomodoroRepository.findPomodorosCount(userId, startDate, endDate, categories, timezone);
		} else if (type.equals("category")) {
			result = pomodoroRepository.findCategoryPomodorosCount(userId, typeId, startDate, endDate, timezone);
		} else if (type.equals("project")) {
			result = pomodoroRepository.findProjectPomodorosCount(userId, typeId, startDate, endDate, timezone);
		} else if (type.equals("task")) {
			result = pomodoroRepository.findTaskPomodorosCount(userId, typeId, startDate, endDate, timezone);
		}
		return result;
	}
	
	public List<Object> retrieveTaskPomodoros(Long userId, Long taskId, int limit, int offset) {
		return pomodoroRepository.findTaskPomodoros(userId, taskId, limit, offset);
	}

	public Integer retrieveTaskPomodorosCount(Long userId, Long taskId) {
		return pomodoroRepository.getTaskPomodorosCount(userId, taskId);
	}
}

