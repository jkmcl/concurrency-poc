package jkml.client;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ClientTests {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void testSendRequestAsync() {
		Client client = new Client();

		String expectedResponse = "Hello client!";

		Message request = new Message("Hello server!");

		MyRequestCallback callback = new MyRequestCallback();

		log.info("Sending request");
		boolean sendTaskResult = client.sendRequestAsync(request, callback);
		assertTrue(sendTaskResult);
		log.info("Request sent");

		log.info("Writing response to underlying channel");
		Channel.getInstance(1).getServerToClientQueue().add(new Message(expectedResponse));

		log.info("Waiting for response");
		await().pollDelay(Duration.ofSeconds(2)).until(callback::isSuccess);

		log.info("Response received: {}", callback.getResponse().getContent());

		client.shutdown();
	}

}
