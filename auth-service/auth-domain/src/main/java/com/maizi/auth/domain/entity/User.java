package com.maizi.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应表：users
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("users")
@Schema(description = "系统用户")
public class User {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名（唯一）", example = "admin")
    private String username;

    @Schema(description = "用户密码（加密后存储）", example = "$2a$10$xxxxxxxx")
    private String password;

    @Schema(description = "用户真实姓名", example = "系统管理员")
    private String fullName;

    @Schema(description = "创建时间", example = "2026-02-11T01:06:05")
    private LocalDateTime createdAt;
}