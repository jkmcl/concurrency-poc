package jkml.util.concurrent;

import java.util.concurrent.SynchronousQueue;

public class MySynchronousQueue<E> extends SynchronousQueue<E> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean offer(E e) {
		try {
			put(e);
			return true;
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

}