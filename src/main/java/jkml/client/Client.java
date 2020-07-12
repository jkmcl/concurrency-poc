package jkml.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.util.concurrent.ConcurrentUtils;

/**
 * Prototype of a proprietary protocol client that talks to a server over
 * multiple full-duplex channels.
 */
public class Client {

	private static final int NUM_CHANNELS = 2;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SimpleObjectPool<ClientChannel> channelPool;

	private ThreadPoolExecutor execServ;

	public Client() {
		channelPool = new SimpleObjectPool<>();
		for (int i = 1; i <= NUM_CHANNELS; ++i) {
			channelPool.addObject(new ClientChannel("DC1-" + Integer.toString(i), i));
		}

		execServ = new ThreadPoolExecutor(NUM_CHANNELS, NUM_CHANNELS, 0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<>(NUM_CHANNELS), ConcurrentUtils.createThreadFactory(Client.class.getSimpleName()));
	}

	public void shutdown() {
		ClientChannel channel;
		while ((channel = channelPool.tryBorrowObject()) != null) {
			channel.close();
		}
		execServ.shutdownNow();
	}

	boolean sendRequestAsync(Message request, RequestCallback callback) {
		log.debug("Submitting request to be sent asynchronously...");
		try {
			execServ.submit(() -> sendRequest(request, callback));
			return true;
		} catch (RejectedExecutionException e) {
			log.error("Failed to submit request to be sent asynchronously ", e);
			return false;
		}
	}

	public void sendRequest(Message request, RequestCallback callback) {
		log.debug("Obtaining idle channel...");
		ClientChannel channel;
		try {
			channel = channelPool.borrowObject();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
		log.debug("Obtained idle channel");

		try {
			Message response = channel.send(request);
			callback.onSuccess(response);
		} catch (Exception e) {
			callback.onFailure(e);
		} finally {
			channelPool.returnObject(channel);
		}
	}

}
