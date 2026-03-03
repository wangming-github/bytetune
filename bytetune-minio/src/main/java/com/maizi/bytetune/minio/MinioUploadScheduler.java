package com.maizi.bytetune.minio;

import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUploadScheduler {

    private final SongService songService;
    private final MinioService minioService;

    // 每10秒执行一次
    // @Scheduled(fixedDelay =
    // TODO 待检查是不是功能重复了
    public void uploadUnprocessedSongs() {

        int batchSize = 5; // 每次处理50条
        List<Song> list = songService.selectUnUploaded(batchSize);
        if (list.isEmpty()) {
            return;
        }
        for (Song song : list) {
            minioService.uploadToMinioAndUpdateState(song);
        }
    }

}