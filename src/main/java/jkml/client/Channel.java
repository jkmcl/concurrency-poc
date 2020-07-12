package jkml.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Channel {

	private static final Map<Integer, Channel> channelMap = new HashMap<>();

	private final int id;

	private final BlockingQueue<Message> clientToServerQueue = new LinkedBlockingQueue<>();

	private final BlockingQueue<Message> serverToClientQueue = new LinkedBlockingQueue<>();

	public static synchronized Channel getInstance(int id) {
		if (channelMap.containsKey(id)) {
			return channelMap.get(id);
		}
		Channel channel = new Channel(id);
		channelMap.put(id, channel);
		return channel;
	}

	private Channel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public BlockingQueue<Message> getClientToServerQueue() {
		return clientToServerQueue;
	}

	public BlockingQueue<Message> getServerToClientQueue() {
		return serverToClientQueue;
	}

}
