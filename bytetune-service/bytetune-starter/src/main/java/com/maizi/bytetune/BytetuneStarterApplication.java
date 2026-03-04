package com.maizi.bytetune;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling // 启用 Spring 定时任务
@SpringBootApplication(scanBasePackages = "com.maizi.bytetune")//
@ConfigurationPropertiesScan("com.maizi.bytetune")
public class BytetuneStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(BytetuneStarterApplication.class, args);
    }

}
