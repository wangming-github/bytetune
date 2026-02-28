package com.maizi.bytetune.minio;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

public class MinioDemo {
    public static void main(String[] args) throws Exception {
        // 初始化客户端
        MinioClient minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000")
                .credentials("minioadmin", "minioadmin").build();

        // 上传文件
        minioClient.uploadObject(UploadObjectArgs.builder().bucket("testbucket")
                .object("墨镜一戴，谁都不爱.jpg")
                .filename("/Users/zimai/Downloads/64132018.jpeg").build());

        System.out.println("上传成功！");
    }
}