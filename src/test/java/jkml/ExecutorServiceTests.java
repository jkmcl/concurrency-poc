package jkml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;

import jkml.util.concurrent.NonQueuingExecutors;

class ExecutorServiceTests {

	private static final int TASK_COUNT = 3;

	private void test(ExecutorService es, int nTasks) {
		CountDownLatch done = new CountDownLatch(nTasks);
		for (int i = 0; i < TASK_COUNT; ++i) {
			es.submit(new MyTask(3, done));
		}

		try {
			done.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		es.shutdown();
	}

	@Test
	void testJdk() {
		ExecutorService es = Executors.newFixedThreadPool(TASK_COUNT - 1);
		assertDoesNotThrow(() -> test(es, TASK_COUNT));
	}

	@Test
	void testCustom() {
		ExecutorService es = NonQueuingExecutors.newFixedThreadPool(TASK_COUNT - 1);
		assertThrows(RejectedExecutionException.class, () -> test(es, TASK_COUNT));
	}

}
