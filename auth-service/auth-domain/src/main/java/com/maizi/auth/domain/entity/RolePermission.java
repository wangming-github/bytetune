package com.maizi.auth.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色权限关联实体
 * 对应表：role_permissions
 */
@Data
@Schema(description = "角色-权限关联关系")
public class RolePermission {

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "权限ID", example = "3")
    private Long permId;
}