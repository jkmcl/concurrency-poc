package jkml.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NonQueuingExecutors {

	private NonQueuingExecutors() {
	}

	/**
	 * Equivalent to {@link Executors#newFixedThreadPool(int)} except that the work
	 * queue used by the created thread pool is a {@link SynchronousQueue}.
	 */
	public static ExecutorService newFixedThreadPool(int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
	}

}
