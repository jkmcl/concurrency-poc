package jkml.config;

import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import jkml.util.concurrent.BlockingThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class MyConfiguration implements SchedulingConfigurer {

	@Bean
	public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
		return builder.build();
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor(TaskExecutorBuilder builder) {
		builder.queueCapacity(0);
		return builder.build(BlockingThreadPoolTaskExecutor.class);
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addFixedDelayTask(new TimeTask(), 10 * 1000L);
	}

}
