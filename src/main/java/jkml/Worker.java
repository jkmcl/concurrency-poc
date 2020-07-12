package jkml;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that does work in a separate thread with a timeout checking task scheduled by the work task.
 */
public class Worker {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ScheduledExecutorService scheduler;

	private Duration workDuration;

	private Duration workTimeout;

	private volatile boolean workDone;

	private volatile boolean workTimedOut;

	private Future<?> workFuture;

	private Future<?> workTimeoutFuture;

	public Worker(Duration workDuration, Duration workTimeout) {
		this.workDuration = workDuration;
		this.workTimeout = workTimeout;
	}

	public boolean isWorkDone() {
		return workDone;
	}

	public boolean isWorkTimedOut() {
		return workTimedOut;
	}

	private void doWork() {
		try {
			log.info("Doing work...");

			log.info("Scheduling task to check timeout in {} second(s)...", workTimeout.getSeconds());
			workTimedOut = false;
			workTimeoutFuture = scheduler.schedule(this::checkTimeOut, workTimeout.getSeconds(), TimeUnit.SECONDS);

			Thread.sleep(workDuration.toMillis());
			workDone = true;
			log.info("Work completed");
		} catch (InterruptedException e) {
			log.info("Work interrupted");
			Thread.currentThread().interrupt();
		}
	}

	private void checkTimeOut() {
		log.info("Checking if work has been done...");
		workTimedOut = !workDone;
		log.info(workTimedOut ? "Work is not done yet" : "Work is done");
	}

	public void startUp() {
		log.info("Starting up...");

		scheduler = Executors.newScheduledThreadPool(2);

		log.info("Starting task to do work for {} second(s)...", workDuration.getSeconds());
		workDone = false;
		workFuture = scheduler.submit(this::doWork);
	}

	public void shutDown() {
		log.info("Shutting down...");

		if (workTimeoutFuture != null && !workTimeoutFuture.isDone()) {
			log.info("Canceling timeout task...");
			workTimeoutFuture.cancel(true);
		}

		if (workFuture != null && !workFuture.isDone()) {
			log.info("Canceling work task...");
			workFuture.cancel(true);
		}

		scheduler.shutdownNow();
	}

}
