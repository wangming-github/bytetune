package com.maizi.bytetune.contract.event.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
                file-service
                    │
                    │ SongSaveEvent
                    ▼
                  Kafka
                    │
                    ▼
               song-service
                    │
                    │ 创建 Song
                    ▼
                MySQL Song
                    │
                    │ songId + filePath
                    ▼
                  Kafka
                    │
                    ▼
             storage-service
                    │
                    │ 上传 MinIO
                    ▼
                  MinIO
                    │
                    │ StorageUploadedEvent
                    ▼
                  Kafka
                    │
                    ▼
               song-service
                    │
                    ▼
          更新 bucketName/objectName
 */

/**
 * 歌曲上传完成返回存储位置信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageToSongEventDto {

    /**
     * 歌曲 ID。
     */
    private Long songId;

    /**
     * 文件 MD5。
     */
    private String md5;

    /**
     * 本地音频文件路径
     */
    private String filePath;

    /**
     * 文件类型 audio/mpeg
     */
    private String contentType;

    /**
     * MinIO Bucket。
     */
    private String bucketName;

    /**
     * MinIO Object。
     */
    private String objectName;

    /**
     * 文件访问 URL（如果需要）。
     */
    private String url;
}