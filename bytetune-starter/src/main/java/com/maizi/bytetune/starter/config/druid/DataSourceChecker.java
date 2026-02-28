package com.maizi.bytetune.starter.config.druid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class DataSourceChecker implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private DataSource dataSource;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("当前数据源类型:{} ", dataSource.getClass().getName());
        log.info("OpenApi页面: http://localhost:8080/redoc/api.html");
        log.info("Druid监控页面: http://localhost:8080/druid/index.html");
        log.info("Minio监控页面: http://localhost:62471/browser/songs");
    }
}