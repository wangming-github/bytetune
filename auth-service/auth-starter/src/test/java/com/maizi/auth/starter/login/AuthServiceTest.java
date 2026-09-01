package com.maizi.auth.starter.login;

import com.maizi.auth.domain.dto.LoginUser;
import com.maizi.auth.domain.dto.R;
import com.maizi.auth.core.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Admin用户登录服务测试")
    void testAdminLogin() {
        // 1️⃣ 调用登录服务，返回统一封装对象
        R<String> response = authService.login("admin", "admin123");

        // 2️⃣ R 对象断言
        assertThat(response).as("响应对象不应该为 null").isNotNull();
        assertThat(response.getCode()).as("状态码应为 200").isEqualTo(200);
        assertThat(response.getMsg()).as("返回消息应为 'success'").isEqualTo("success");

    }
}