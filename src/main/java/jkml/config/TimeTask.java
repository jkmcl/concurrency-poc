package jkml.config;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeTask implements Runnable {

	private final Logger log = LoggerFactory.getLogger(TimeTask.class);

	@Override
	public void run() {
		log.info("Current time: {}", Instant.now());
	}

}
