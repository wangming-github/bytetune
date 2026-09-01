package com.maizi.auth.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.UUID;

// Spring Security 核心配置
// 主要作用：
// 1 关闭CSRF
// 2 配置无状态Session
// 3 配置接口访问权限
// 4 注册JWT认证过滤器
// 开启方法级权限控制
@Configuration
@EnableMethodSecurity(prePostEnabled = true)// prePostEnabled = true 才能使用 @PreAuthorize / @PostAuthorize
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    // Redis模板
    // JwtAuthenticationFilter 用于读取用户权限缓存
    private final StringRedisTemplate redisTemplate;

    public SecurityConfig(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Spring Security 过滤器链
    // Security 所有安全控制都通过该链完成
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 初始化 traceId（用于系统启动日志链路）
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        log.info("初始化 Spring Security 过滤器链");
        http
                // 1 关闭 CSRF
                // JWT 架构通常关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2 Session策略
                // 设置为无状态（Stateless）  不使用 HttpSession 存储认证信息  每个请求都通过 JWT 认证
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3 配置接口访问权限
                .authorizeHttpRequests(auth -> auth
                        // 登录接口放行
                        .requestMatchers("/auth/login").permitAll()
                        // 其他接口必须认证
                        .anyRequest().authenticated())
                // 4 注册 JWT 认证过滤器
                // 放在 UsernamePasswordAuthenticationFilter 之前  因为 JWT 要先解析
                .addFilterBefore(new JwtAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class);
        log.info("JWT认证过滤器注册成功");
        // 清理 MDC
        MDC.clear();
        return http.build();
    }
}