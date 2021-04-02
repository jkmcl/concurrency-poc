package jkml.util.concurrent;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadUtils {

	private ThreadUtils() {
	}

	public static ThreadFactory createThreadFactory(String namePrefix) {
		return new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").build();
	}

}
