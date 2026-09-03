package com.maizi.bytetune.file.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * ApplicationStartingEvent
 * ↓
 * “我要启动了”
 * <p>
 * ApplicationEnvironmentPreparedEvent
 * ↓
 * “配置环境准备好了”
 * <p>
 * ApplicationContextInitializedEvent
 * ↓
 * “Spring 容器初始化了”
 * <p>
 * ApplicationPreparedEvent
 * ↓
 * “容器准备好了，Bean 还没完全干完”
 * <p>
 * ContextRefreshedEvent
 * ↓
 * “Spring Context 刷新完成” （⭕才有日志输出）
 * <p>
 * ApplicationStartedEvent
 * ↓
 * “Spring Boot 基本启动了”
 * <p>
 * CommandLineRunner
 * ApplicationRunner
 * ↓
 * “执行启动初始化任务”
 * <p>
 * ApplicationReadyEvent
 * ↓
 * “彻底启动完成，可以接请求了”
 * <p>
 * ApplicationFailedEvent
 * ↓
 * “启动他妈失败了”
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class ListenerAppStartup {


    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        // Java 17：Text Blocks """ XXXXX """
        log.info("""
                
                ╔═══════════════════════════════════════════════════════════╗
                ║            🚀📁 bytetune-file-service 启动成功             ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }

    /**
     * 应用启动失败。
     */
    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {

        log.error("""
                
                ╔═══════════════════════════════════════════════════════════╗
                ║              ❗ bytetune-file-service  启动失败            ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }
}