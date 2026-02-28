package com.bytetune.config;

import com.bytetune.config.properties.MinioProperties;
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
        // 创建 MinioClient
        MinioClient client = MinioClient.builder().endpoint(minio.getEndpoint()).credentials(minio.getAccessKey(), minio.getSecretKey()).build();

        // 获取默认 bucket 名称
        String bucketName = minio.getBucketName();

        // 检查 bucket 是否存在，不存在则创建
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        log.info("默认bucket:{},创建完成", bucketName);
        return client;
    }
}