package jkml.util.concurrent;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ConcurrentUtils {

	private ConcurrentUtils() {
	}

	public static ThreadFactory createThreadFactory(String namePrefix) {
		return new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").build();
	}

}
