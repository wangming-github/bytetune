package com.maizi.bytetune.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 歌曲上传事件，用于描述一次歌曲文件的上传信息，供消息中间件传递和消费。
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongUploadEventDTO {
    /**
     * 歌曲唯一ID，消费端用来保证幂等性。
     */
    private Long songId;
    /**
     * 歌曲名字
     */
    private String songName;
    /**
     * MinIO 桶名，存储歌曲文件的桶。
     */
    private String bucketName;
    /**
     * MinIO 对象名，歌曲文件在桶中的对象名。
     */
    private String objectName;
    /**
     * 本地路径或临时 URL，指向歌曲文件的实际位置。
     */
    private String filePath;
    /**
     * 事件生成时间，时间戳格式。
     */
    private Long timestamp;
    /**
     * 重试次数，消费端可根据此字段判断重试逻辑。
     */
    private Integer retryCount;
}