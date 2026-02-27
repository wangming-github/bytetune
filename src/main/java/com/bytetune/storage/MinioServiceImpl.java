package com.bytetune.storage;

import com.bytetune.config.properties.MinioProperties;
import com.bytetune.entity.Song;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

import static com.baomidou.mybatisplus.extension.toolkit.Db.updateById;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties minioProperties;

    @Override
    public void uploadToMinio(Song song, String objectName) {

        File file = new File(song.getPath());
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        try {
            minioClient.putObject(PutObjectArgs.builder()//
                    .bucket(minioProperties.getBucketName())//
                    .object(objectName)//
                    .stream(new FileInputStream(file), file.length(), -1)//
                    .contentType(song.getContentType()).build());//
        } catch (Exception e) {
            log.warn("上传文件失败:{},{}", objectName, e.getMessage());
        }

    }
}
