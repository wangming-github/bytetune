package com.maizi.auth.starter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * AuthService 测试断言模板（单类运行示例）
 * <p>
 * - 独立运行，无需数据库或 Spring 容器
 * - 展示各种集合、字符串、异常断言写法
 * - 可直接拷贝做自己项目断言模板
 */
class AuthServiceTestTemplate {

    // 模拟 LoginUser 对象
    static class TestLoginUser {
        String username;
        List<String> roles;
        List<String> permissions;

        TestLoginUser(String username, List<String> roles, List<String> permissions) {
            this.username = username;
            this.roles = roles;
            this.permissions = permissions;
        }

        public String getUsername() {
            return username;
        }

        public List<String> getRoles() {
            return roles;
        }

        public List<String> getPermissions() {
            return permissions;
        }
    }

    @Test
    @DisplayName("管理员登录测试 - 断言模板示例")
    void testAdminLoginAssertions() {
        // 模拟数据
        TestLoginUser testLoginUser = new TestLoginUser("admin", Arrays.asList("ADMIN"), Arrays.asList("SONG_READ", "SONG_UPLOAD", "SONG_DELETE", "MANAGE_USER"));

        // -----------------------------
        // 1️⃣ 基础断言
        // -----------------------------
        assertThat(testLoginUser).isNotNull();
        assertThat(testLoginUser.getUsername()).isEqualTo("admin");
        assertThat(testLoginUser.getRoles()).isNotEmpty();
        assertThat(testLoginUser.getPermissions()).isNotEmpty();

        // -----------------------------
        // 2️⃣ 集合断言
        // -----------------------------
        assertThat(testLoginUser.getRoles()).contains("ADMIN"); // 包含元素
        assertThat(testLoginUser.getPermissions()).contains("SONG_READ", "SONG_DELETE"); // 包含多个元素
        assertThat(testLoginUser.getPermissions()).doesNotContain("READ"); // 不包含某个元素
        assertThat(testLoginUser.getPermissions()).hasSizeGreaterThan(3); // 集合大小

        // -----------------------------
        // 3️⃣ 字符串断言
        // -----------------------------
        assertThat(testLoginUser.getUsername()).startsWith("adm").endsWith("min");
        assertThat(testLoginUser.getUsername()).contains("ad");

        // -----------------------------
        // 4️⃣ Lambda / 高级断言
        // -----------------------------
        assertThat(testLoginUser.getPermissions()).allSatisfy(p -> assertThat(p).contains("_")); // 所有权限名称包含下划线
        assertThat(testLoginUser.getPermissions()).anySatisfy(p -> assertThat(p).startsWith("SONG")); // 至少一个以 SONG 开头

        // -----------------------------
        // 5️⃣ 组合断言
        // -----------------------------
        assertThat(testLoginUser).extracting(TestLoginUser::getRoles, TestLoginUser::getPermissions).satisfies(tuple -> {
            List<String> roles = (List<String>) tuple.get(0);
            List<String> perms = (List<String>) tuple.get(1);

            assertThat(roles).contains("ADMIN");
            assertThat(perms).contains("SONG_READ");
        });
    }

    @Test
    @DisplayName("异常断言示例")
    void testExceptionAssertions() {
        // 模拟方法抛异常
        Runnable loginFail = () -> {
            throw new RuntimeException("用户不存在");
        };

        // AssertJ 异常断言
        // assertThatThrownBy(loginFail)
        //         .isInstanceOf(RuntimeException.class)
        //         .hasMessageContaining("不存在");
        //
        // // JUnit5 原生异常断言
        // Throwable thrown = org.junit.jupiter.api.Assertions.assertThrows(
        //         RuntimeException.class,
        //         loginFail
        // );
        // assertThat(thrown.getMessage()).contains("不存在");
    }
}