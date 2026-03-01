package com.maizi.bytetune.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
public class ThreadConfig {

    /**
     * 自定义 ThreadPoolTaskScheduler 线程池
     * 类型：TaskScheduler，Spring 专门用于 定时任务（@Scheduled）
     * 方法调用：支持定时调度，例如 schedule(), scheduleAtFixedRate()
     *
     * @return ThreadPoolTaskScheduler
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);// 设置线程池大小，根据你的定时任务数量和并发情况调整
        scheduler.setThreadNamePrefix("Scheduler-"); // 设置线程名前缀，日志中线程名将显示为 -MyScheduler-1、-MyScheduler-2 等
        scheduler.setDaemon(false);  // 是否使用守护线程，false 表示非守护线程，JVM 退出前会等待任务完成
        return scheduler;  // 初始化线程池
    }

    /**
     * 定义 TaskExecutor，用于异步执行扫描任务
     * 类型：TaskExecutor，Spring 通用异步线程池，用于 普通异步任务（@Async 或手动提交 Runnable/Callable）
     * 方法调用：只支持立即执行任务 execute() 或提交 submit()
     * ⚠️ 如果存在多个 TaskExecutor Bean，Spring 就会报 NoUniqueBeanDefinitionException，必须用 @Qualifier 或 @Primary。
     */
    @Primary
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);      // 核心线程数
        executor.setMaxPoolSize(1);       // 最大线程数
        executor.setQueueCapacity(10);    // 队列容量
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}