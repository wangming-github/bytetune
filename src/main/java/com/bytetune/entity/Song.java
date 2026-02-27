package com.bytetune.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "歌曲实体")
public class Song implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "歌曲ID", example = "1")
    private Long id;

    @Schema(description = "歌曲名称", example = "夜曲")
    private String name;

    @Schema(description = "歌手")
    private String artist;

    @Schema(description = "专辑")
    private String album;

    @Schema(description = "时长（秒）", example = "260")
    private Integer duration;

    @Schema(description = "文件绝对路径或相对路径", example = "/Users/zimai/Music/yequ.mp3")
    private String path;

    @Schema(description = "文件 MD5，用于去重", example = "1a79a4d60de6718e8e5b326e338ae533")
    private String md5;

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "对象名称(UUID路径)")
    private String objectName;

    @Schema(description = "文件类型", example = "audio/mpeg")
    private String contentType;

    @Schema(description = "文件大小(字节)", example = "5242880")
    private Long size;

    @Schema(description = "封面桶")
    private String coverBucket;

    @Schema(description = "封面对象名")
    private String coverObject;

    @Schema(description = "0未上传 1已上传 2失败")
    private int status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}