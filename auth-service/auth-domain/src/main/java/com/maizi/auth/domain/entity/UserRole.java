package com.maizi.auth.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色关联实体
 * 对应表：user_roles
 */
@Data
@Schema(description = "用户-角色关联关系")
public class UserRole {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "角色ID", example = "2")
    private Long roleId;
}