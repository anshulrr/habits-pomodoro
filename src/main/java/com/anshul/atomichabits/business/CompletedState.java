package com.anshul.atomichabits.business;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompletedState implements PomodoroState {

	@Override
	public void markStarted(RunningPomodoro runningPomodoro) {
		log.debug("from completed state: mark started");
		// do nothing
		// TODO: throw error
	}

	@Override
	public void markPaused(RunningPomodoro runningPomodoro) {
		log.debug("from completed state: mark paused");
		// do nothing
	}

	@Override
	public void markCompleted(RunningPomodoro runningPomodoro) {
		log.debug("from completed state: mark completed");
		// do nothing
	}

	@Override
	public void update(RunningPomodoro runningPomodoro) {
		log.debug("from completed state: update");
		// do nothing
	}
}
