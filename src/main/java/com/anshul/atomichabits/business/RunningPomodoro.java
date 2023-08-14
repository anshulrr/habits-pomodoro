package com.anshul.atomichabits.business;

import com.anshul.atomichabits.model.Pomodoro;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunningPomodoro {
	
	private PomodoroState startedState;
	private PomodoroState pausedState;
	private PomodoroState completedState;

	private PomodoroState currentState;
	
	private Pomodoro pomodoro;
	
	public RunningPomodoro(Pomodoro p) {
		log.debug("from running pomodoro: {}", p);
		pomodoro = p;
		
		startedState = new StartedState();
		pausedState = new PausedState();
		completedState = new CompletedState();
		
		currentState = startedState;
		if (pomodoro.getStatus().equals("started")) {
			currentState = startedState;
		} else if (pomodoro.getStatus().equals("paused")) {
			currentState = pausedState;
		} else if (pomodoro.getStatus().equals("completed")) {
			currentState = completedState;
		}
	}

	public Pomodoro getPomodoro() {
		return pomodoro;
	}
	
	public void updateStatus(String status) {
		log.debug("from running pomodoro: update status: {}", status);
		if (status.equals("started")) {
			currentState.markStarted(this);
		} else if (status.equals("paused")) {
			currentState.markPaused(this);
		} else if (status.equals("completed")) {
			currentState.markCompleted(this);
		}
	}
	
	public void updatePomodoroData() {
		currentState.update(this);
	}

	public void setCurrentState(PomodoroState state) {
        currentState = state;
    }
	
    public PomodoroState getStartedState() {
		return startedState;
	}

	public PomodoroState getPausedState() {
		return pausedState;
	}

	public PomodoroState getCompletedState() {
		return completedState;
	}
	
}
