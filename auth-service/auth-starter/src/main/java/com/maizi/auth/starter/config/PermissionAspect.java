package com.maizi.auth.starter.config;

import com.maizi.auth.util.CheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ⚠️ 你这个方案的关键点（别踩坑）
 * ❗1. 权限来源必须对齐
 * JWT / DB 里必须是这种格式：
 * -song:read
 * -song:create
 * <p>
 * ❗2. SecurityConfig 要放行 AOP
 * 别被 @PreAuthorize 和这个冲突搞懵：
 * 👉 推荐二选一：
 * -@PreAuthorize ⭐⭐⭐⭐⭐
 * -AOP自定义 ⭐⭐⭐
 * <p>
 * ❗3. 权限 vs 角色 别混
 * 类型 示例
 * -角色 ROLE_ADMIN
 * -权限 song:read
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(checkPermission)")
    public Object check(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {

        // 1️⃣ 获取认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("未登录");
        }

        // 2️⃣ 获取权限列表
        List<String> perms = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        String requiredPerm = checkPermission.value();

        // 3️⃣ 权限校验
        if (!perms.contains(requiredPerm)) {
            log.warn("权限不足 -> 需要: {}, 当前: {}", requiredPerm, perms);
            throw new AccessDeniedException("无权限");
        }

        // 4️⃣ 放行
        return joinPoint.proceed();
    }
}