package com.maizi.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求 DTO
 * LoginRequest 在接口测试里就是 模拟前端请求的 DTO（Data Transfer Object），它的作用就是把前端传过来的 JSON 映射成 Java 对象。
 */
@Data
@Builder
@Schema(description = "登录请求参数")
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "密码", example = "admin123")
    private String password;
}