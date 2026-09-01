package com.maizi.auth.api.controller;

import com.maizi.auth.core.service.AuthService;
import com.maizi.auth.domain.dto.LoginRequest;
import com.maizi.auth.domain.dto.R;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 1 AuthService.login()
 * TODO
 * 2 JWT生成
 * 3 JWT过滤器
 * 4 SecurityContext
 * 5 @PreAuthorize权限控制
 * 6 Redis缓存用户权限
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<String> login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }
}