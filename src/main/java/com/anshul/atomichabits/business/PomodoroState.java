package com.anshul.atomichabits.business;

public interface PomodoroState {
	
	public void markStarted(RunningPomodoro runningPomodoro);
	
	public void markPaused(RunningPomodoro runningPomodoro);
	
	public void markCompleted(RunningPomodoro runningPomodoro);
	
	public void update(RunningPomodoro runningPomodoro);
}
