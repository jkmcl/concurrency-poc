package jkml.util.concurrent;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NonQueuingExecutorsTests {

	private static final int THREAD_POOL_SIZE = 2;

	private final Logger log = LoggerFactory.getLogger(NonQueuingExecutorsTests.class);

	private final ExecutorService goodExecSvc = NonQueuingExecutors.newFixedThreadPool(THREAD_POOL_SIZE);

	@Test
	void pollForMessages() throws InterruptedException {

		int totalCount = 10;
		int submitCount = 0;
		MyService myService = new MyService();

		boolean rejected = false;

		List<String> messages = findMessages(totalCount, submitCount);
		for (String msg : messages) {
			try {
				goodExecSvc.submit(() -> myService.processMessage(msg));
				++submitCount;
			} catch (RejectedExecutionException e) {
				log.debug("No idle thread");
				rejected = true;
				break;
			}
		}

		assertTrue(rejected);

		goodExecSvc.shutdown();
		goodExecSvc.awaitTermination(5, TimeUnit.SECONDS);
	}

	private List<String> findMessages(int totalCount, int submitCount) {
		List<String> messages = new ArrayList<>();
		for (int i = submitCount; i < totalCount; ++i) {
			messages.add(new String(Integer.toString(i)));
		}
		return messages;
	}

}
