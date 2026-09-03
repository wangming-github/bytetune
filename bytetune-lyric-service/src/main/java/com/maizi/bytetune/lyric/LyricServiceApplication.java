package com.maizi.bytetune.lyric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * bytetune-song-service 核心服务
 * ↓
 * OpenFeign 远程调用
 * ↓
 * lyric-service 歌词解析，上传到MongoDB
 */
@SpringBootApplication
public class LyricServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyricServiceApplication.class, args);
    }

}
