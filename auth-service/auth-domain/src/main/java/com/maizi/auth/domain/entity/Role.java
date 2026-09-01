package com.maizi.auth.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体
 * 对应表：roles
 */
@Data
@Schema(description = "角色信息")
public class Role {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称（唯一）", example = "ADMIN")
    private String roleName;

    @Schema(description = "角色描述", example = "平台管理员")
    private String description;

    @Schema(description = "创建时间", example = "2026-02-11T01:06:05")
    private LocalDateTime createdAt;
}