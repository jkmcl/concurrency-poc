package jkml;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WorkerTests {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void testWorkDoneBeforeTimeout() throws Exception {
		log.info("Testing work done before timeout...");
		Worker worker = new Worker(Duration.ofSeconds(3), Duration.ofSeconds(4));
		worker.startUp();
		await().until(worker::isWorkDone);
		await().until(worker::isWorkTimedOut, is(false));
		worker.shutDown();
		log.info("Test finished");
	}

	@Test
	void testWorkDoneAfterTimeout() throws Exception {
		log.info("Testing work done after timeout...");
		Worker worker = new Worker(Duration.ofSeconds(3), Duration.ofSeconds(2));
		worker.startUp();
		await().until(worker::isWorkTimedOut);
		await().until(worker::isWorkDone);
		worker.shutDown();
		log.info("Test finished");
	}

	@Test
	void testWorkDoneAtSameTimeAsTimeout() throws Exception {
		log.info("Testing work done at the same time as timeout...");
		Worker worker = new Worker(Duration.ofSeconds(3), Duration.ofSeconds(3));
		worker.startUp();
		await().pollDelay(Duration.ofSeconds(3)).until(worker::isWorkDone);
		worker.shutDown();
		log.info("Test finished");
	}

	@Test
	void testShutdownBeforeWorkDone() throws Exception {
		log.info("Testing shutdown before work done...");
		Worker worker = new Worker(Duration.ofSeconds(3), Duration.ofSeconds(4));
		worker.startUp();
		await().pollDelay(Duration.ofSeconds(1)).until(worker::isWorkDone, is(false));
		worker.shutDown();
		log.info("Test finished");
	}


}
