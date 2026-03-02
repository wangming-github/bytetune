package com.maizi.bytetune.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class YamlPrinterListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ConfigurableEnvironment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        environment.getPropertySources().forEach(ps -> {
            String name = ps.getName();
            if (name.contains("application-") && name.contains(".yml")) {
                log.info("已加载:{}", name);
            }
        });
    }
}