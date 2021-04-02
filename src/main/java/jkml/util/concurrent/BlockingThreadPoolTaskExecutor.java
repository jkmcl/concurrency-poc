package jkml.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class BlockingThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

	private static final long serialVersionUID = 1L;

	@Override
	protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
		if (queueCapacity > 0) {
			return new LinkedBlockingQueue<>(queueCapacity);
		}
		else {
			return new MySynchronousQueue<>();
		}
	}

}
