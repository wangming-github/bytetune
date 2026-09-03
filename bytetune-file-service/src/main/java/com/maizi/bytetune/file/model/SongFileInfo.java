package com.maizi.bytetune.file.model;

import com.maizi.bytetune.common.constants.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 “我扫描到了一个本地音乐文件，它有什么信息？”
 它的生命周期：
     本地 MP3
       ↓
    AudioFileProcessor
       ↓
    SongFileInfo
 */
@Data
@Builder // Entity 需要无参构造函数（默认要求），Lombok 的 @Builder 不会生成无参构造，所以最好保留 @NoArgsConstructor：
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "本地音乐文件解析结果 bytetune-file-service 模块内部使用")
public class SongFileInfo implements Serializable {

    public Long id;

    @Schema(description = "歌曲名称", example = "夜曲")
    public String name;

    @Schema(description = "歌手")
    public String artist;

    @Schema(description = "专辑")
    public String album;

    @Schema(description = "时长（秒）", example = "260")
    public Integer duration;

    @Schema(description = "文件绝对路径或相对路径", example = "/Users/zimai/Music/yequ.mp3")
    public String path;

    @Schema(description = "文件 MD5，用于去重", example = "1a79a4d60de6718e8e5b326e338ae533")
    public String md5;

    @Schema(description = "存储桶名称")
    public String bucketName;

    @Schema(description = "对象名称(UUID路径)")
    public String objectName;

    @Schema(description = "文件类型", example = "audio/mpeg")
    public String contentType;

    @Schema(description = "文件大小(字节)", example = "5242880")
    public Long size;

    @Schema(description = "封面桶")
    public String coverBucket;

    @Schema(description = "封面对象名")
    public String coverObject;

    /**
     * 这个 @Builder.Default 是 Lombok 提供的注解，用在带 @Builder 的类字段上，
     * 用于 给 Builder 提供默认值，否则 Builder 会把字段初始化为 Java 的默认值（0、null、false），而不是你在字段上写的默认值。
     */
    @Builder.Default
    @Schema(description = "0未上传 1已上传 2失败")
    public int status = UploadStatus.NOT_UPLOADED.getCode(); // Builder 默认值

    @Schema(description = "创建时间")
    @Builder.Default
    public LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "更新时间")
    public LocalDateTime updatedAt;
}