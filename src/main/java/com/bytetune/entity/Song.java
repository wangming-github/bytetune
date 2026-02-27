package com.bytetune.entity;

import com.bytetune.util.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder // Entity 需要无参构造函数（默认要求），Lombok 的 @Builder 不会生成无参构造，所以最好保留 @NoArgsConstructor：
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * @Builder.Default 是 Lombok 提供的注解，用在带 @Builder 的类字段上，
     * 用于 给 Builder 提供默认值，否则 Builder 会把字段初始化为 Java 的默认值（0、null、false），而不是你在字段上写的默认值。
     */
    @Builder.Default
    @Schema(description = "0未上传 1已上传 2失败")
    private int status = UploadStatus.NOT_UPLOADED.getCode(); // Builder 默认值

    @Schema(description = "创建时间")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}