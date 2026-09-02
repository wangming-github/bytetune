package com.maizi.bytetune.lyric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * bytetune-service
 * ↓
 * OpenFeign
 * ↓
 * lyric-service
 */
@SpringBootApplication
public class LyricServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyricServiceApplication.class, args);
    }

}
