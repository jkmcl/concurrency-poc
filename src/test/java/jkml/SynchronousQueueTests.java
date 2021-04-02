package jkml;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronousQueueTests {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void test() {
		log.info("Test started");

		SynchronousQueue<String> sq = new SynchronousQueue<>();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

		scheduler.schedule(() -> {
			log.info("Taking...");
			try {
				String item = sq.take();
				log.info("Taken: {}", item);
			} catch (InterruptedException e) {
				log.info("Take interrupted");
				Thread.currentThread().interrupt();
			}
		}, 1, TimeUnit.SECONDS);

		scheduler.schedule(() -> {
			log.info("Offering...");
			boolean offerResult = sq.offer("token");
			log.info(offerResult ? "Offered" : "Not offered");
		}, 2, TimeUnit.SECONDS);

		await().pollDelay(Duration.ofSeconds(5)).until(sq::isEmpty);

		log.info("Test ended");
	}

	@Test
	void test2() {
		log.info("Test started");

		SynchronousQueue<String> sq = new SynchronousQueue<>();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

		scheduler.schedule(() -> {
			log.info("Polling...");
			String item = sq.poll();
			log.info(item == null ? "No element available" : "Element available");
		}, 1, TimeUnit.SECONDS);

		await().pollDelay(Duration.ofSeconds(5)).until(sq::isEmpty);

		log.info("Test ended");
	}


}
