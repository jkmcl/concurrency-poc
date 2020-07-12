package jkml.client;

import java.net.ProtocolException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.util.concurrent.ConcurrentUtils;

class ClientChannel {

	private final Logger log;

	private final ExecutorService execServ;

	private final Object sendLock = new Object();

	private final String id;

	private final BlockingQueue<Object> inboundMessageQueue = new SynchronousQueue<>();

	private Channel channel;

	private Future<?> inboundMessageFutureTask;

	public ClientChannel(String id, int i) {
		this.id = id;
		log = LoggerFactory.getLogger(ClientChannel.class.getName() + "." + id);
		execServ = Executors.newSingleThreadExecutor(ConcurrentUtils.createThreadFactory(ClientChannel.class.getSimpleName() + "-" + id));

		log.info("Client channel created - ID: {}", id);
		channel = Channel.getInstance(i);
		log.info("Underlying channel created - ID: {}", channel.getId());

		// Starts loop that simulates underlying network library receiving inbound
		// messages from the channel
		// asynchronously
		inboundMessageFutureTask = execServ.submit(() -> {
			while (!Thread.interrupted()) {
				try {
					log.info("Waiting for inbound message");
					Message message = channel.getServerToClientQueue().take();
					log.info("Received inbound message. Calling inbound message handler");
					inboundMessageHandler(message);
				} catch (InterruptedException e) {
					log.info("Inbound message listener task interrupted!");
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public void close() {
		log.debug("Closing client channel");
		if (inboundMessageFutureTask != null) {
			log.info("Shutting down inbound message task");
			inboundMessageFutureTask.cancel(true);
		}
		execServ.shutdownNow();
		log.debug("Client channel closed");
	}

	public String getId() {
		return id;
	}

	Channel getChannel() {
		return channel;
	}

	void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void inboundMessageHandler(Message message) throws InterruptedException {
		log.debug("Receives inbound message");
		if (message.equals(Message.CORRUPTED)) {
			inboundMessageQueue.put(new Exception("Simulated exception occurred"));
		} else {
			inboundMessageQueue.put(message);
		}
	}

	public Message send(Message request) throws ProtocolException, InterruptedException {
		log.debug("Sending request: {}", request.getContent());
		synchronized (sendLock) {
			channel.getClientToServerQueue().add(request);
		}
		log.debug("Sent request");

		log.debug("Waiting for response");
		Object object = inboundMessageQueue.take();

		if (object instanceof Throwable) {
			log.debug("Caught and rethrowing exception during processing of response");
			throw new ProtocolException();
		} else {
			log.debug("Received response");
			return (Message) object;
		}
	}

}
