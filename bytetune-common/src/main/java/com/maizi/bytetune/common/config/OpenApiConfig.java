package com.maizi.bytetune.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * OpenAPI 配置类
 *
 * <p>该类用于自定义 Springdoc OpenAPI 文档信息，包括：
 * <ul>
 *     <li>API 标题、版本、描述</li>
 *     <li>左上角 Logo（x-logo 扩展字段）</li>
 * </ul>
 * 生成的文档可在 Swagger UI 或 ReDoc 中访问。
 */
@Configuration
public class OpenApiConfig {

    /**
     * 自定义 OpenAPI Bean
     *
     * <p>配置 API 文档的基础信息，并添加 x-logo 扩展字段，用于 ReDoc 左上角显示 Logo。
     *
     * @return OpenAPI 对象，用于 Springdoc 自动生成文档
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ByteTune API")                          // 文档标题
                        .version("1.0.0")                               // 文档版本
                        .description("ByteTune 音乐库 API 文档，包含歌曲管理接口") // 文档描述
                        .extensions(Map.of("x-logo", Map.of(
                                "url", "/redoc/logo.svg",         // 静态资源路径
                                "backgroundColor", "#ffffff",     // 背景色
                                "altText", "ByteTune Logo",       // Alt文本
                                "href", "http://localhost:8080/redoc/api.html" // 点击跳转首页
                        )))
                );
    }
}