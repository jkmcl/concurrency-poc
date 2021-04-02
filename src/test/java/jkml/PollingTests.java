package jkml;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.service.MyService;
import jkml.util.concurrent.NonQueuingExecutors;

class PollingTests {

	private static final int THREAD_POOL_SIZE = 3;

	private static final long SLEEP_TIME_MS = 100L;

	private final Logger log = LoggerFactory.getLogger(PollingTests.class);

	private final ExecutorService goodExecSvc = NonQueuingExecutors.newFixedThreadPool(THREAD_POOL_SIZE);

	@Test
	void pollDatabase() {

		int totalCount = 10;
		int submitCount = 0;
		MyService myService = new MyService();

		while (true) {

			List<String> messages = findMessages(totalCount, submitCount);
			for (String msg : messages) {
				try {
					goodExecSvc.submit(() -> myService.processMessage(msg));
					++submitCount;
				} catch (RejectedExecutionException e) {
					log.debug("No idle thread");
					break;
				}
			}

			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

	}

	private List<String> findMessages(int totalCount, int submitCount) {
		List<String> messages = new ArrayList<>();
		for (int i = submitCount; i < totalCount; ++i) {
			messages.add(new String(Integer.toString(i)));
		}
		return messages;
	}

}
