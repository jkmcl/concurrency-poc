package jkml.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleObjectPool<T> {

	private BlockingQueue<T> blockingQueue = new LinkedBlockingQueue<>();

	public void addObject(T object) {
		blockingQueue.add(object);
	}

	public T borrowObject() throws InterruptedException {
		return blockingQueue.take();
	}

	public T tryBorrowObject() {
		return blockingQueue.poll();
	}

	public void returnObject(T object) {
		blockingQueue.add(object);
	}

}
