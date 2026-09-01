package com.maizi.auth.starter.login;

import com.maizi.auth.domain.dto.LoginRequest;
import com.maizi.auth.domain.dto.LoginUser;
import com.maizi.auth.domain.dto.R;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AuthController 集成测试类
 * <p>
 * 说明：
 * 1. @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * - 启动整个 Spring Boot 应用上下文
 * - 使用随机端口启动 Web 环境，防止端口冲突
 * 2. TestRestTemplate
 * - Spring 提供的测试专用 REST 客户端
 * - 可发送 HTTP 请求并接收响应对象
 * 3. 适合测试 Controller + Service + Repository 全链路
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    /**
     * @LocalServerPort 注入随机启动的端口号
     */
    @LocalServerPort
    private int port;

    /**
     * 注入 TestRestTemplate，用于模拟 HTTP 请求
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 测试登录接口
     * 测试点：
     * 1. 登录返回对象非空
     * 2. user.username 正确
     * 3. roles 包含 "ADMIN"
     * 4. permissions 包含 "SONG_READ"
     * <p>
     * 注意：
     * - 确保数据库中有 admin 用户及对应角色和权限
     * - 如果 response.getUser() 为 null，说明 Controller 没有返回 LoginUser
     */
    @Test
    @DisplayName("Admin用户登录接口测试（启动整个 Spring Context） ")
    void testLogin() {
        // 1️⃣ 构建请求 URL
        String url = "http://localhost:" + port + "/auth/login";

        // 2️⃣ 构建登录请求 DTO
        LoginRequest request = LoginRequest.builder().username("admin").password("admin123").build();

        // 3️⃣ 使用 exchange + ParameterizedTypeReference 返回泛型安全对象
        ResponseEntity<R<String>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request), //
                new ParameterizedTypeReference<R<String>>() {
                });

        R<String> response = responseEntity.getBody();

        // 4️⃣ 断言 R 对象非空
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMsg()).isEqualTo("success");

        // 5️⃣ 获取实际数据
        String string = response.getData();
        assertThat(string).isNotNull();

    }
}