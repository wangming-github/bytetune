package com.maizi.bytetune.starter;

import com.maizi.bytetune.common.constants.UploadStatus;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.service.MinioService;
import com.maizi.bytetune.common.service.SongService;
import com.maizi.bytetune.minio.MinioProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    SongService songService;
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
            MDC.put("songId", "[" + song.getName() + "]");
            MDC.put("job", "[上传&更新]");
            // TODO 从数据库中查询出信息 拼接
            // 规则: audio/{artistId}_{安全歌手名}/{albumId}_{安全专辑名}/{songId}_{安全歌曲名}.{suffix}
            // 输出: audio/1024_柴田淳/202602_秋桜/1827364518273645123_秋桜.mp3
            String objectName = "audio/" + song.getName();
            // 上传并获取状态
            int status = minioService.uploadToMinio(song, objectName) ? UploadStatus.SUCCESS.getCode() : UploadStatus.FAILED.getCode();
            // 更新数据库
            songService.updateMinioStatus(song.getId(), status, minio.getBucketName(), objectName);
            MDC.clear();
        }
    }
}