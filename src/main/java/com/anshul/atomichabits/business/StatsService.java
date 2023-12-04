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
	
	public List<Object> retrieveProjectCategoriesPomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		List<Object> result = pomodoroRepository.findProjectCategoriesTime(user_id, startDate, endDate, categories);
		return result;
	}
	
	public List<Object> retrieveProjectPomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		List<Object> result = pomodoroRepository.findProjectsTime(user_id, startDate, endDate, categories);
		return result;
	}
	
	public List<Object> retrieveTaskPomodoros(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories) {
		List<Object> result = pomodoroRepository.findTasksTime(user_id, startDate, endDate, categories);
		return result;
	}
	
	public Map<String, TotalChartProjectData> retrieveTotalPomodoros(Long user_id, String limit, OffsetDateTime startDate, OffsetDateTime endDate, long[] categories, String timezone) {
		if (limit.equals("daily")) {
			limit = "DD";
		} else if (limit.equals("weekly")) {
			limit = "IW";
		} else if (limit.equals("monthly")) {
			limit = "MM";
		}
		
		List<String[]> result = pomodoroRepository.findTotalTime(user_id, startDate, endDate, categories, timezone, limit);
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
	
	public List<Object> retrievePomodorosCount(Long user_id, OffsetDateTime startDate, OffsetDateTime endDate, String type, Long typeId, String timezone) {
		List<Object> result = null;
		if (type.equals("user")) {
			result = pomodoroRepository.findPomodorosCount(user_id, startDate, endDate, timezone);
		} else if (type.equals("category")) {
			result = pomodoroRepository.findCategoryPomodorosCount(user_id, typeId, startDate, endDate, timezone);
		} else if (type.equals("project")) {
			result = pomodoroRepository.findProjectPomodorosCount(user_id, typeId, startDate, endDate, timezone);
		}
		return result;
	}
}

