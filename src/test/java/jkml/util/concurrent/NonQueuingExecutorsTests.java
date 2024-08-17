package jkml.util.concurrent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NonQueuingExecutorsTests {

	private static final int TASK_COUNT = 3;

	private final Logger log = LoggerFactory.getLogger(NonQueuingExecutorsTests.class);

	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		log.info("# Executing test: {}", testInfo.getDisplayName());
	}

	private void test(ExecutorService es, int nTasks) {
		var done = new CountDownLatch(nTasks);
		for (var i = 0; i < TASK_COUNT; ++i) {
			es.submit(new MyTask(2, done));
		}

		try {
			done.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		es.shutdown();
	}

	@Test
	void testNewFixedThreadPool_original() {
		var es = Executors.newFixedThreadPool(TASK_COUNT - 1);
		assertDoesNotThrow(() -> test(es, TASK_COUNT));
	}

	@Test
	void testNewFixedThreadPool() {
		var es = NonQueuingExecutors.newFixedThreadPool(TASK_COUNT - 1);
		assertThrows(RejectedExecutionException.class, () -> test(es, TASK_COUNT));
	}

}
