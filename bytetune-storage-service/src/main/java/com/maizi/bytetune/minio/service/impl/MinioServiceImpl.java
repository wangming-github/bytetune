package com.maizi.bytetune.minio.service.impl;

import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import com.maizi.bytetune.contract.event.storage.StorageToSongEventDto;
import com.maizi.bytetune.minio.service.MinioService;
import com.maizi.bytetune.minio.config.MinioConfProperties;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioConfProperties minioProperties;

    @Override
    public ObjectWriteResponse uploadToMinio(SongToStorageEventDto song, String fileName) {
        File file = new File(song.getPath());
        if (!file.exists()) {
            log.error("文件不存在: {}", song.getPath());
            return null;
        }

        try {
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()//
                    .object(fileName)//
                    .contentType(song.getContentType())//
                    .bucket(minioProperties.getBucketName())//
                    .stream(new FileInputStream(file), file.length(), -1)//
                    .build());
            log.info("歌曲文件存储文件服务器完成：{}", response);
            return response;
        } catch (Exception e) {
            log.warn("歌曲文件存储文件服务器失败....");
            return null;
        }
    }

}
