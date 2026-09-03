package com.maizi.bytetune.song.listener;

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

    private final DataSource dataSource;
    private final ConfigurableEnvironment environment;

    @EventListener
    public void onApplicationStarting(ApplicationStartingEvent event) {
        log.debug("\uD83D\uDD1C1.应用开始启动（）");
    }

    @EventListener
    public void onEnvironmentPrepared(ApplicationEnvironmentPreparedEvent event) {

        Environment environment = event.getEnvironment();
        log.debug("\uD83D\uDD1C2.当前环境：{}", environment.getActiveProfiles());
    }

    @EventListener
    public void onContextInitialized(ApplicationContextInitializedEvent event) {
        log.debug("\uD83D\uDD1C3.ApplicationContext 初始化");
    }

    @EventListener
    public void onPrepared(ApplicationPreparedEvent event) {
        log.debug("\uD83D\uDD1C4.ApplicationContext 已准备");
    }

    // ApplicationStartingEvent
    // ApplicationEnvironmentPreparedEvent
    // ApplicationContextInitializedEvent
    //@Component所以它还没有被Spring 创建出来的时候，前面几个事件已经发生了。
    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        log.debug("\uD83D\uDD1C5.Spring Context 刷新完成");
    }

    @EventListener
    public void onStarted(ApplicationStartedEvent event) {
        log.debug("\uD83D\uDD1C6. ApplicationStarted");
        // throw new RuntimeException("💣 故意测试：ContextRefreshed 后启动失败");
    }

    public void onApplicationEvent(ApplicationReadyEvent event) {

        environment.getPropertySources().forEach(ps -> {
            String name = ps.getName();
            if (name.contains("application-") && name.contains(".yml")) {
                log.info("⭕已加载:{}", name);
            }
        });
    }

    public void dataSourceAndDoc(ApplicationReadyEvent event) {
        log.info("❗当前数据源类型:{} ", dataSource.getClass().getName());
        log.info("❗OpenApi页面: http://localhost:8080/redoc/api.html");
        log.info("❗Druid监控页面: http://localhost:8080/druid/index.html");
        log.info("❗Minio监控页面: http://127.0.0.1:9000  minioadmin/minioadmin");
        log.info("❗Minio启动脚本: /Users/zimai/Documents/dev/tools_脚本工具/Minio/start-minio.sh");
        log.info("❗Kafka重置/启动: bytetune-service/bytetune-common/com/maizi/bytetune/common/kafka/kafka配置.md");
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        onApplicationEvent(event);
        dataSourceAndDoc(event);
        // Java 17：Text Blocks """ XXXXX """
        log.info("""
                
                ╔═══════════════════════════════════════════════════════════╗
                ║            🚀🎧 bytetune-song-service 启动成功             ║
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
                ║              ❗ bytetune-song-service  启动失败            ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }
}