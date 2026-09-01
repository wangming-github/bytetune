package com.maizi.auth.starter.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maizi.auth.api.controller.AuthController;
import com.maizi.auth.domain.dto.LoginRequest;
import com.maizi.auth.domain.dto.LoginUser;
import com.maizi.auth.domain.dto.R;
import com.maizi.auth.domain.entity.User;
import com.maizi.auth.core.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 测试类（Web 层单元测试）
 * <p>
 * 说明：
 * 1. @WebMvcTest(AuthController.class)
 * - 只加载 Controller 层相关 Bean，不启动整个 Spring Context
 * - MockMvc 用于模拟 HTTP 请求
 * 2. MockMvc 结合 ObjectMapper 可以方便地发送 JSON 请求并验证响应
 * 3. 该测试类独立运行，不依赖数据库或完整的 Service 实现（可结合 @MockBean 模拟 Service）
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ 模拟 AuthService，给 AuthController 注入
    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Admin用户登录接口测试（不启动整个 Spring Context） ")
    void testLogin() throws Exception {
        // 模拟登录返回结果
        LoginUser loginUser = LoginUser.builder().user(User.builder().username("admin").build()).roles(List.of("ADMIN")).permissions(List.of("SONG_READ", "SONG_UPLOAD")).build();

        // 让 mock authService 返回 R.ok(loginUser)
        when(authService.login("admin", "admin123")).thenReturn(R.ok("token-123"));

        LoginRequest request = LoginRequest.builder().username("admin").password("admin123").build();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON) //
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()) //
                .andExpect(jsonPath("$.code").value(200)).andExpect(jsonPath("$.msg") //
                        .value("success")).andExpect(jsonPath("$.data.user.username").value("admin")) //
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN")).andExpect(jsonPath("$.data.permissions").isArray()).andExpect(jsonPath("$.data.permissions[0]").value("SONG_READ"));
    }
}