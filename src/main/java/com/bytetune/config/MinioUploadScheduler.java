package com.bytetune.config;

import com.bytetune.config.properties.MinioProperties;
import com.bytetune.entity.Song;
import com.bytetune.service.ISongService;
import com.bytetune.storage.MinioService;
import com.bytetune.util.UploadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUploadScheduler {

    @Autowired
    MinioProperties minio;
    @Autowired
    ISongService songService;
    @Autowired
    MinioService minioService;

    // 每60秒执行一次
    @Scheduled(fixedDelay = 60_000)
    public void uploadUnprocessedSongs() {

        int batchSize = 5; // 每次处理50条
        List<Song> list = songService.selectUnUploaded(batchSize);
        if (list.isEmpty()) {
            return;
        }
        for (Song song : list) {
            // 从数据库中查询出信息 拼接
            // 规则: audio/{artistId}_{安全歌手名}/{albumId}_{安全专辑名}/{songId}_{安全歌曲名}.{suffix}
            // 输出: audio/1024_柴田淳/202602_秋桜/1827364518273645123_秋桜.mp3
            String objectName = "audio/" + song.getName();
            try {
                minioService.uploadToMinio(song, objectName);
                songService.updateMinioStatus(song.getId(), UploadStatus.SUCCESS.getCode(), minio.getBucketName(), objectName);
                log.info("{}上传minio完成", song.getPath());
            } catch (Exception e) {
                log.error("上传失败 id={}", song.getId(), e);
                songService.updateMinioStatus(song.getId(), UploadStatus.FAILED.getCode(), minio.getBucketName(), objectName);
            }
        }
    }
}