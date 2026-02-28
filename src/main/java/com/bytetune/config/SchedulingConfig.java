package com.bytetune.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务线程池配置类
 * <p>
 * 用于自定义 Spring @Scheduled 任务的线程池，包括线程数量、线程名前缀等。
 * 默认情况下，Spring 使用的线程池线程名为 "scheduling-N"，通过这里可以自定义名称，
 * 方便日志追踪和任务区分。
 * </p>
 */
@Configuration
@EnableScheduling // 启用 Spring 定时任务
public class SchedulingConfig {

    /**
     * 自定义 ThreadPoolTaskScheduler 线程池
     *
     * @return ThreadPoolTaskScheduler
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 设置线程池大小，根据你的定时任务数量和并发情况调整
        scheduler.setPoolSize(5);

        // 设置线程名前缀，日志中线程名将显示为 -MyScheduler-1、-MyScheduler-2 等
        scheduler.setThreadNamePrefix("task-");

        // 是否使用守护线程，false 表示非守护线程，JVM 退出前会等待任务完成
        scheduler.setDaemon(false);

        // 初始化线程池
        return scheduler;
    }
}