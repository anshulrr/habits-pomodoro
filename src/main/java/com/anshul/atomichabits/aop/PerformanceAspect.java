package com.anshul.atomichabits.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {
	@Around("execution(* com.anshul.atomichabits.controller.*.*(..))")
	public Object monitorSlowMethods(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();

		// 1. Before method execution
		stopWatch.start(joinPoint.getSignature().getName());

		try {
			// 2. Execute the actual method
			return joinPoint.proceed();
		} finally {
			// 3. After method execution (runs even if an exception occurs)
			stopWatch.stop();
			long timeTaken = stopWatch.getTotalTimeMillis();

			// Standard: Only log if it's actually "slow" (e.g., > 500ms)
			if (timeTaken > 500) {
				log.warn("PERF ALERT: {} took {}ms", joinPoint.getSignature().toShortString(), timeTaken);
			} else if (timeTaken > 200) {
				log.trace("PERF CHECK: {} took {}ms", joinPoint.getSignature().toShortString(), timeTaken);
			}
		}
	}
}
