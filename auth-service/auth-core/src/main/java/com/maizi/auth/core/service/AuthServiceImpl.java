package com.maizi.auth.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maizi.auth.domain.dto.LoginUser;
import com.maizi.auth.domain.dto.R;
import com.maizi.auth.domain.entity.User;
import com.maizi.auth.domain.mapper.PermissionMapper;
import com.maizi.auth.domain.mapper.RoleMapper;
import com.maizi.auth.domain.mapper.UserMapper;
import com.maizi.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AuthServiceImpl
 * <p>
 * 认证授权核心业务服务
 * <p>
 * 职责：
 * 1 用户身份认证（Authentication）
 * 2 用户角色查询（RBAC）
 * 3 用户权限查询
 * 4 JWT Token 生成
 * <p>
 * 日志设计：
 * 采用 MDC 记录 traceId 与 userId，方便日志链路追踪
 * <p>
 * MDC字段：
 * traceId  请求链路ID
 * userId   当前用户ID
 * <p>
 * 登录流程：
 * <p>
 * login()
 * │
 * ▼
 * 1 查询用户
 * 2 校验密码
 * 3 查询角色
 * 4 查询权限
 * 5 生成JWT
 * 6 返回token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT Token
     */
    @Override
    public R<String> login(String username, String password) {

        // 生成 traceId（请求级日志追踪）
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        log.info("==================================");
        log.info("用户登录请求开始 username={}", username);
        try {

            // ==============================
            // 1️⃣ 查询用户
            // ==============================
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

            if (user == null) {
                log.warn("登录失败 用户不存在 username={}", username);
                return R.error("用户不存在", 401);
            }

            // 写入 MDC
            MDC.put("userId", String.valueOf(user.getId()));
            log.info("用户存在 userId={} username={}", user.getId(), user.getUsername());

            // ==============================
            // 2️⃣ 校验密码
            // ==============================
            if (!user.getPassword().equals(password)) {
                log.warn("登录失败 密码错误 userId={} username={}", user.getId(), username);
                return R.error("用户名或密码错误", 401);
            }

            log.info("密码校验通过 userId={}", user.getId());

            // ==============================
            // 3️⃣ 查询角色
            // ==============================
            List<String> roles = roleMapper.selectRolesByUserId(user.getId());
            log.info("角色查询完成 userId={} roles={}", user.getId(), roles);
            // ==============================
            // 4️⃣ 查询权限
            // ==============================
            List<String> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
            log.info("权限查询完成 userId={} permissionsCount={}", user.getId(), permissions.size());
            // ==============================
            // 5️⃣ 生成 JWT Token
            // ==============================
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            claims.put("roles", roles);

            String token = JwtUtils.generateToken(claims);
            log.info("JWT生成成功 userId={},token={}", user.getId(), token);
            // ==============================
            // 6️⃣ 构建 LoginUser
            // ==============================
            LoginUser loginUser = LoginUser.builder().user(user).roles(roles).permissions(permissions).build();
            log.info("用户登录成功 userId={} roles={} permissions={}", user.getId(), roles.size(), permissions.size());
            return R.ok(token);
        } finally {
            // 清理 MDC，避免线程复用污染
            MDC.clear();
        }
    }
}