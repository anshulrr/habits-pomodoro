package com.anshul.atomichabits.business;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.anshul.atomichabits.model.Pomodoro;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PausedState implements PomodoroState {

	@Override
	public void markStarted(RunningPomodoro runningPomodoro) {
		log.debug("from paused state: mark started");
		Pomodoro pomodoro = runningPomodoro.getPomodoro();

		// TODO: find better solution: needed for timeElapsed calculation in further state change
		// Update the startTime, so that refresh api and sync logic works correctly
		OffsetDateTime updatedStartTime = OffsetDateTime.now(ZoneOffset.UTC)
				.minusSeconds(pomodoro.getTimeElapsed());
		log.trace(updatedStartTime + " : " + pomodoro.getTimeElapsed());
		pomodoro.setStartTime(updatedStartTime);
		
		pomodoro.setStatus("started");
		runningPomodoro.setCurrentState(runningPomodoro.getStartedState());
	}

	@Override
	public void markPaused(RunningPomodoro runningPomodoro) {
		log.debug("from paused state: mark paused");
		// do nothing
	}

	@Override
	public void markCompleted(RunningPomodoro runningPomodoro) {
		log.debug("from paused state: mark completed");
		Pomodoro pomodoro = runningPomodoro.getPomodoro();
		
		// update endTime
		pomodoro.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));

		pomodoro.setStatus("completed");
		runningPomodoro.setCurrentState(runningPomodoro.getCompletedState());
	}

	@Override
	public void update(RunningPomodoro runningPomodoro) {
		log.debug("from paused state: update");
		// do nothing
	}
}
