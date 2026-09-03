package com.maizi.bytetune.contract.event.song;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
        | 对象                      | 所在模块                 | 职责            |
        | ------------------------ | ----------------------- | ------------- |
        | `SongFileInfo`           | `bytetune-file`         | 本地文件解析结果      |
        | `SongStorageInfo`        | `bytetune-storage`/file | 对象存储信息        |
        | `SongCreateRequest`      | `song-service`          | 创建歌曲 API 请求   |
        | `SongResponse`           | `song-service`          | API 返回        |
        | `SongUploadRequestEvent` | `bytetune-event`        | 歌曲上传事件        |
        | `SongCreatedEvent`       | `bytetune-event`        | 歌曲创建事件        |
        | `Song`                   | `song-service`          | Song 业务/数据库实体 |
 */

/**
 * 歌曲文件上传事件。
 * <p> 用于描述歌曲文件及其对象存储信息， 供消息中间件进行跨服务传递。 * </p>
 * 扫描本地歌曲 → Kafka → 上传 MinIO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileToSongEventDto {
    /**
     * 歌曲唯一 ID。
     */
    private Long songId;
    /**
     * 歌曲名称。
     */
    private String songName;
    /**
     * 音频时长，单位：秒。
     */
    private Integer duration;
    /**
     * 文件 MIME 类型。
     */
    private String contentType;
    /**
     * 文件大小，单位：字节。
     */
    private Long size;
    /**
     * 文件 MD5。
     */
    private String md5;
    /**
     * 本地文件绝对路径。
     */
    private String filePath;

    /**
     * MinIO Bucket。
     */
    private String bucketName;
    /**
     * MinIO Object。
     */
    private String objectName;

    /**
     * 封面 Bucket。
     */
    private String coverBucket;
    /**
     * 封面 Object。
     */
    private String coverObject;
}