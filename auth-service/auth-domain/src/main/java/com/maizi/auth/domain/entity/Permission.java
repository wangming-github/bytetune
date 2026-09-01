package com.maizi.auth.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体
 * 对应表：permissions
 */
@Data
@Schema(description = "权限信息")
public class Permission {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "权限标识（唯一）", example = "SONG_UPLOAD")
    private String permName;

    @Schema(description = "权限描述", example = "上传歌曲")
    private String description;

    @Schema(description = "创建时间", example = "2026-02-11T01:06:05")
    private LocalDateTime createdAt;
}