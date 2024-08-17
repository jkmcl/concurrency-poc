package jkml.util.concurrent;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronousQueueTests {

	private final Logger log = LoggerFactory.getLogger(SynchronousQueueTests.class);

	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		log.info("# Executing test: {}", testInfo.getDisplayName());
	}

	@Test
	void testTake() {
		var sq = new SynchronousQueue<String>();

		var scheduler = Executors.newScheduledThreadPool(2);

		var instants = new Instant[2];

		scheduler.submit(() -> {
			log.info("Taking...");
			try {
				instants[0] = Instant.now();
				var item = sq.take();
				instants[1] = Instant.now();
				log.info("Taken: {}", item);
			} catch (InterruptedException e) {
				log.info("Take interrupted");
				Thread.currentThread().interrupt();
			}
		});

		scheduler.schedule(() -> {
			log.info("Offering...");
			boolean offerResult = sq.offer("token");
			log.info(offerResult ? "Offered" : "Not offered");
		}, 2, TimeUnit.SECONDS);

		await().pollDelay(Duration.ofSeconds(3)).until(sq::isEmpty);
		assertTrue(Duration.between(instants[0], instants[1]).compareTo(Duration.ofSeconds(1)) >= 0);
	}

	@Test
	void testPoll() {
		var sq = new SynchronousQueue<String>();

		var scheduler = Executors.newScheduledThreadPool(2);

		var instants = new Instant[2];

		scheduler.submit(() -> {
			log.info("Polling...");
			instants[0] = Instant.now();
			var item = sq.poll();
			instants[1] = Instant.now();
			log.info(item == null ? "No element available" : "Element available");
		});

		await().pollDelay(Duration.ofSeconds(3)).until(sq::isEmpty);
		assertTrue(Duration.between(instants[0], instants[1]).compareTo(Duration.ofSeconds(1)) < 0);
	}

}
