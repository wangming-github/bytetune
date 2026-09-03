package com.maizi.bytetune.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
┌──────────────────────┐
│  bytetune-file       │
│                      │
│ 文件扫描              │
│ 文件监听              │
│ Metadata解析         │
│ MD5                  │
└──────────┬───────────┘
           │
           │ SongFileEvent
           ▼
      ┌──────────┐
      │  Kafka   │
      └────┬─────┘
           │
           ▼
┌────────────────────────────┐
│ bytetune-song-service      │
│                            │
│ 消费事件                   │
│ 业务校验                   │
│ 去重                       │
│ Song构建                   │
│ CRUD                       │
│                            │
│          MySQL             │
└────────────────────────────┘



bytetune-file
    │
    │ 只认识：
    │ SongFileInfo / SongFileEvent
    ▼
Kafka
    │
    ▼
bytetune-song-service
    │
    │ 只认识：
    │ Song / SongService / SongMapper
    ▼
MySQL
 */

//默认扫描的是com.maizi.bytetune.file 但是依赖的包存在com.maizi.bytetune.messaging.kafka
@SpringBootApplication(scanBasePackages = "com.maizi.bytetune")
public class BytetuneFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(BytetuneFileApplication.class, args);
    }

}
