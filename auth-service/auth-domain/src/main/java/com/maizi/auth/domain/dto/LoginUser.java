package com.maizi.auth.domain.dto;

import com.maizi.auth.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 响应 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录用户信息")
public class LoginUser {

    @Schema(description = "用户信息")
    private User user;

    @Schema(description = "用户角色列表", example = "[ADMIN,USER]")
    private List<String> roles;

    @Schema(description = "用户权限列表", example = "[user:add,user:delete]")
    private List<String> permissions;

}