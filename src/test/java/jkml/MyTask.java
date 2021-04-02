package jkml;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTask implements Runnable {

	private final Logger log = LoggerFactory.getLogger(MyTask.class);

	private final int sleepSec;

	private final CountDownLatch done;

	public MyTask(int sleepSec, CountDownLatch done) {
		this.sleepSec = sleepSec;
		this.done = done;
	}

	@Override
	public void run() {
		try {
			log.info("Start of task");
			Thread.sleep(sleepSec * 1000);
			log.info("End of task");
			done.countDown();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
