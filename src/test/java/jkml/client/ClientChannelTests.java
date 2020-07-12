package jkml.client;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.ProtocolException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.util.concurrent.ConcurrentUtils;

class ClientChannelTests {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void testNormalResponse() throws Exception {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4,
				ConcurrentUtils.createThreadFactory(ClientChannelTests.class.getSimpleName()));

		ClientChannel clientChannel = new ClientChannel("01", 1);

		String expectedResponse = "This is the response";

		Future<Message> requestFuture = scheduler.submit(() -> {
			log.info("Sending request");
			Message request = new Message("This is a request");
			return clientChannel.send(request);
		});

		Future<?> responseTask = scheduler.schedule(() -> {
			log.info("Writing response to underlying inbound channel");
			clientChannel.getChannel().getServerToClientQueue().add(new Message(expectedResponse));
		}, 2, TimeUnit.SECONDS);

		await().pollDelay(Duration.ofSeconds(2)).until(responseTask::isDone);
		await().until(requestFuture::isDone);

		Message response = requestFuture.get();
		log.info("Response: {}", response.getContent());
		assertEquals(expectedResponse, response.getContent());

		clientChannel.close();

		scheduler.shutdownNow();
	}

	@Test
	void testException() throws Exception {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4,
				ConcurrentUtils.createThreadFactory(ClientChannelTests.class.getSimpleName()));

		ClientChannel clientChannel = new ClientChannel("01", 1);

		Future<Message> requestFuture = scheduler.submit(() -> {
			log.info("Sending request");
			Message request = new Message("This is a request");
			return clientChannel.send(request);
		});

		Future<?> responseTask = scheduler.schedule(() -> {
			log.info("Writing response to underlying inbound channel");
			clientChannel.getChannel().getServerToClientQueue().add(Message.CORRUPTED);
		}, 2, TimeUnit.SECONDS);

		await().pollDelay(Duration.ofSeconds(2)).until(responseTask::isDone);
		await().until(requestFuture::isDone);

		try {
			requestFuture.get();
			fail("ExecutionException was not thrown");
		} catch (ExecutionException ee) {
			log.info("Caught ExecutionException");
			assertTrue(ee.getCause() instanceof ProtocolException);
		}

		clientChannel.close();

		scheduler.shutdownNow();
	}

}
