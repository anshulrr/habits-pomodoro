package com.anshul.atomichabits.business;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.anshul.atomichabits.model.Pomodoro;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartedState implements PomodoroState {

	@Override
	public void markStarted(RunningPomodoro runningPomodoro) {
		log.debug("from started state: mark started");
		update(runningPomodoro);
		// do nothing
	}

	@Override
	public void markPaused(RunningPomodoro runningPomodoro) {
		log.debug("from started state: mark paused");
		Pomodoro pomodoro = runningPomodoro.getPomodoro();
		
		// calculate and update timeElapsed
		Long timeElapsed = Duration.between(pomodoro.getStartTime(), OffsetDateTime.now()).getSeconds();
		if (timeElapsed <= pomodoro.getLength() * 60) {
			pomodoro.setTimeElapsed(Math.toIntExact(timeElapsed));
		} else {
			log.info("Pause: calculated timeElapsed: {}, pomodoro length: {}", timeElapsed, pomodoro.getLength() * 60);
			pomodoro.setTimeElapsed(pomodoro.getLength() * 60);
		}
		log.debug("{}", pomodoro.getTimeElapsed());
		
		pomodoro.setStatus("paused");
		runningPomodoro.setCurrentState(runningPomodoro.getPausedState());
	}

	@Override
	public void markCompleted(RunningPomodoro runningPomodoro) {
		log.debug("from started state: mark completed");
		Pomodoro pomodoro = runningPomodoro.getPomodoro();
		
		// calculate and update timeElapsed
		Long timeElapsed = Duration.between(pomodoro.getStartTime(), OffsetDateTime.now()).getSeconds();
		if (timeElapsed <= pomodoro.getLength() * 60) {
			pomodoro.setTimeElapsed(Math.toIntExact(timeElapsed));
		} else {
			log.info("Complete: calculated timeElapsed: {}, pomodoro length: {}", timeElapsed, pomodoro.getLength() * 60);
			pomodoro.setTimeElapsed(pomodoro.getLength() * 60);
		}
		log.debug("{}", pomodoro.getTimeElapsed());
		
		// update endTime
		pomodoro.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));

		pomodoro.setStatus("completed");
		runningPomodoro.setCurrentState(runningPomodoro.getCompletedState());
	}

	@Override
	public void update(RunningPomodoro runningPomodoro) {
		log.debug("from started state: update");
		Pomodoro pomodoro = runningPomodoro.getPomodoro();
		
		// calculate and update timeElapsed
		Long timeElapsed = Duration.between(pomodoro.getStartTime(), OffsetDateTime.now()).getSeconds();
		if (timeElapsed < pomodoro.getLength() * 60) {
			pomodoro.setTimeElapsed(Math.toIntExact(timeElapsed));
		} else {
			pomodoro.setTimeElapsed(pomodoro.getLength() * 60 - 3);
		}
		log.debug("{}", pomodoro.getTimeElapsed());
	}
}
