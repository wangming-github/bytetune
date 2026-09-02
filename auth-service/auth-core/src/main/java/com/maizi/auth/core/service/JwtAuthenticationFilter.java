package com.maizi.auth.core.service;

import com.maizi.auth.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ============================================================
 * JwtAuthenticationFilter
 * ============================================================
 * <p>
 * JWT 认证过滤器
 * <p>
 * 作用：
 * 在每一个 HTTP 请求进入 Controller 之前执行 JWT 认证逻辑。
 * <p>
 * 该过滤器会：
 * <p>
 * 1 解析 Authorization Header 中的 JWT
 * 2 校验 Token 合法性
 * 3 从 Redis 查询用户权限
 * 4 构建 Spring Security Authentication 对象
 * 5 写入 SecurityContext
 * <p>
 * 这样 Controller 层就可以直接获取认证信息。
 * <p>
 * ------------------------------------------------------------
 * 过滤器执行流程
 * ------------------------------------------------------------
 * <p>
 * HTTP Request
 * │
 * ▼
 * JwtAuthenticationFilter
 * │
 * ├── 1 获取 Authorization Header
 * │
 * ├── 2 解析 JWT Token
 * │
 * ├── 3 获取 userId / username
 * │
 * ├── 4 Redis 查询权限
 * │
 * ├── 5 构建 Authentication
 * │
 * ├── 6 写入 SecurityContext
 * │
 * ▼
 * Controller
 * ------------------------------------------------------------
 * MDC 日志链路
 * ------------------------------------------------------------
 * 每个请求生成 traceId：
 * traceId = UUID
 * 写入 MDC：
 * MDC.put("traceId", traceId)
 * 日志输出：
 * [traceId] JWT解析成功
 * 这样一条请求的所有日志都能串起来。
 * ------------------------------------------------------------
 * Redis 权限缓存
 * ------------------------------------------------------------
 * key: auth:perm:{userId}
 * value: SONG_READ,SONG_UPLOAD,SONG_DELETE
 * <p>
 * ------------------------------------------------------------
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    // Redis操作模板
    // 用于读取用户权限缓存
    private final StringRedisTemplate redisTemplate;

    public JwtAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 每个HTTP请求都会执行一次
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 1 生成traceId（日志链路追踪）
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        // 2 写入MDC
        MDC.put("traceId", traceId);
        try {
            // 3 获取Authorization Header
            // 示例：  Authorization: Bearer xxx.jwt.token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                // 4 去掉Bearer前缀
                token = token.substring(7);
                // 5 解析JWT
                Claims claims = JwtUtils.parseToken(token);
                String userId = claims.get("userId").toString();
                String username = claims.get("username").toString();
                log.info("JWT解析成功 userId={} username={}", userId, username);
                // 6 从Redis读取权限
                String perms = redisTemplate.opsForValue().get("auth:perm:" + userId);
                List<String> permissions = perms != null ? Arrays.asList(perms.split(",")) : List.of();
                // 7 转换为Spring Security权限对象
                List<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                // 8 构建Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                // 9 写入SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("用户认证成功 username={} permissions={}", username, permissions);
            }
            log.info("没有获取到Bearer，继续执行过滤器链。");
            // 10 继续执行过滤器链
            chain.doFilter(request, response);
        } catch (Exception e) {
            // JWT解析失败
            log.error("JWT认证失败", e);
            // 清理认证信息
            SecurityContextHolder.clearContext();

        } finally {
            // 清理MDC 防止线程复用导致traceId污染
            MDC.clear();
        }
    }
}