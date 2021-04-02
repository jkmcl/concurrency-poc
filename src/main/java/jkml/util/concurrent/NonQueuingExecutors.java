package jkml.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NonQueuingExecutors {

	private NonQueuingExecutors() {
	}

	public static ExecutorService newFixedThreadPool(int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
	}

	public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
				threadFactory);
	}

}
