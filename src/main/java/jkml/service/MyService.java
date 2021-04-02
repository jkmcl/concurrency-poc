package jkml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {

	private final Logger log = LoggerFactory.getLogger(MyService.class);

	public void processMessage(String msg) {
		try {
			log.info("Start processing message: {}", msg);
			Thread.sleep(3 * 1000L);
			log.info("Finish processing message: {}", msg);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
