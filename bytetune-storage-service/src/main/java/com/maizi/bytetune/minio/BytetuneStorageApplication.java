package com.maizi.bytetune.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ByteTune Storage Service 启动类
 */
@SpringBootApplication(scanBasePackages = "com.maizi.bytetune")
public class BytetuneStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BytetuneStorageApplication.class, args);
    }
}