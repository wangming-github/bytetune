package com.maizi.bytetune.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@Order(99)
@RequiredArgsConstructor
public class ListenerDataSource implements ApplicationListener<ApplicationReadyEvent> {

    private final DataSource dataSource;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("当前数据源类型:{} ", dataSource.getClass().getName());
        log.info("╔═════════════════════════════════════════════════════╗");
        log.info("║ OpenApi页面: http://localhost:8080/redoc/api.html");
        log.info("║ Druid监控页面: http://localhost:8080/druid/index.html");
        log.info("║ Minio监控页面: http://127.0.0.1:9000  minioadmin/minioadmin");
        log.info("║ Minio启动脚本: /Users/zimai/Documents/dev/tools_脚本工具/Minio/start-minio.sh");
        log.info("║ Kafka安装位置: /Users/zimai/Documents/dev/env/kafka/kafka_2.13-4.2.0");
        log.info("║ Kafka启动命令: bin/kafka-server-start.sh config/server.properties &");
        log.info("╚═════════════════════════════════════════════════════╝");
    }
}