package com.maizi.bytetune.minio;

import com.maizi.bytetune.common.constants.UploadStatus;
import com.maizi.bytetune.common.kafka.KafkaSongEventDTO;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.mapper.SongMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka 消费者服务
 * 负责消费 song-upload-group Topic 中的 SongUploadEventDTO 消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioUploadConsumer {

    private final MinioService minioService;
    private final SongMapper songMapper;

    /**
     * 监听 Kafka Topic: "song-upload-group"
     * groupId 可以指定消费组 ID
     *
     * @param event 消息对象
     */
    @KafkaListener(topics = "${bytetune.kafka.topic.name}")// groupId已经全局配置
    public void consume(KafkaSongEventDTO event) {
        MDC.put("job", "[消费消息→上传文件→修改状态]");
        // 打印消息
        log.info("收到 Kafka 消息 ObjectName: {}", event.getSongName());
        Song song = songMapper.selectById(event.getSongId());
        log.debug("mysql查询该数据信息：{}", song.toString());
        // ✅ 幂等判断
        if (UploadStatus.SUCCESS.getCode() == (song.getStatus())) {
            log.info("已上传成功，跳过 id={}", song.getId());
            return;
        }
        minioService.uploadToMinioAndUpdateState(song);
    }
}