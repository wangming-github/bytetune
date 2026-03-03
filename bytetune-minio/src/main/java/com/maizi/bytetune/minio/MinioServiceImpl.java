package com.maizi.bytetune.minio;

import com.maizi.bytetune.common.constants.UploadStatus;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.service.SongService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final SongService songService;
    private final MinioClient minioClient;
    private final MinioConfProperties minioProperties;

    @Override
    public boolean uploadToMinio(Song song, String objectName) {
        File file = new File(song.getPath());
        if (!file.exists()) {
            log.error("文件不存在: {}", song.getPath());
            return false;
        }

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectName).stream(new FileInputStream(file), file.length(), -1).contentType(song.getContentType()).build());
            log.info("上传minio完成!");
            return true;
        } catch (Exception e) {
            log.error("上传失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * @param song
     * @return
     */
    @Override
    public void uploadToMinioAndUpdateState(Song song) {
        String objectName = "audio/" + song.getName();
        // 上传并获取状态 更新数据库
        int status = uploadToMinio(song, objectName) ? UploadStatus.SUCCESS.getCode() : UploadStatus.FAILED.getCode();
        songService.updateMinioStatus(song.getId(), status, minioProperties.getBucketName(), objectName);
        log.info("上传状态更新为:{},完成!", UploadStatus.fromCode(status));
        MDC.clear();
    }
}
