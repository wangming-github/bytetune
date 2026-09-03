package com.maizi.bytetune.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
bytetune-song-service
│
├── 歌曲业务
│   ├── 歌曲新增
│   ├── 歌曲查询
│   ├── 歌曲修改
│   ├── 歌曲删除
│   └── 歌曲状态管理
│
├── 歌曲元数据
│   ├── 歌曲名
│   ├── 歌手
│   ├── 专辑
│   ├── 时长
│   ├── MD5
│   └── 文件类型 / 大小
│
├── 歌曲文件生命周期
│   ├── 文件是否存在
│   ├── 文件上传状态
│   ├── 文件存储状态
│   └── 文件处理状态
│
├── 对外 API
│   ├── GET    /songs
│   ├── GET    /songs/{id}
│   ├── POST   /songs
│   ├── PUT    /songs/{id}
│   └── DELETE /songs/{id}
│
└── 服务间通信
    ├── → bytetune-file-service
    │     文件扫描 / 文件信息
    │
    ├── → bytetune-minio-service
    │     文件上传 / 下载 / 删除
    │
    └── → bytetune-lyric-service
          歌词查询 / 管理


                         ByteTune
                            │
                            ▼
                   ┌─────────────────┐
                   │  API / Gateway  │
                   └────────┬────────┘
                            │
              ┌─────────────┼──────────────┐
              ▼             ▼              ▼
        song-service   lyric-service   auth-service
              │
        ┌─────┴─────┐
        ▼           ▼
 file-service   minio-service

 */
@EnableAsync
@EnableScheduling // 启用 Spring 定时任务
@SpringBootApplication(scanBasePackages = "com.maizi.bytetune")//
@ConfigurationPropertiesScan("com.maizi.bytetune")
public class BytetuneSongApplication {
    public static void main(String[] args) {
        SpringApplication.run(BytetuneSongApplication.class, args);
    }

}
