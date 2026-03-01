package com.maizi.bytetune.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio 配置类
 * <p>
 * 1. 根据 application.yml 中配置的 minio 信息初始化 MinioClient。
 * 2. 检查默认存储桶是否存在，不存在则自动创建。
 * 3. 提供 MinioClient Bean 供项目中上传/下载文件使用。
 */
@Slf4j
@Configuration
public class MinioConfig {

    @Autowired
    MinioProperties minio;

    /**
     * 创建并初始化 MinioClient Bean
     *
     * @return 初始化后的 MinioClient 实例
     * @throws Exception 当连接 Minio 或创建 bucket 失败时抛出异常
     */
    @Bean
    public MinioClient minioClient() throws Exception {
        try {
            // 构建客户端（不会立即发起请求）
            MinioClient client = MinioClient.builder().endpoint(minio.getEndpoint()).credentials(minio.getAccessKey(), minio.getSecretKey()).build();
            // 获取默认 bucket 名称
            String bucketName = minio.getBucketName();

            // 尝试访问 MinIO 创建默认 bucket
            try {
                boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!exists) {
                    client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
                log.info("默认bucket:{},创建完成", bucketName);
            } catch (Exception e) {
                // 捕获网络异常或服务未启动异常
                log.warn("⚠️ MinIO 未启动，默认 bucket:{},初始化失败: {}", bucketName, e.getMessage());
            }
            return client;
        } catch (Exception e) {
            // 构建客户端失败时捕获异常，避免 Spring 启动报错
            log.error("❌ MinioClient 初始化失败: {}", e.getMessage());
            // 返回一个空的客户端（或者可以考虑返回 null，需要 @Nullable 注解处理）
            return null;
        }
    }
}