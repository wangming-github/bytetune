package com.maizi.bytetune.song.messaging;

import com.maizi.bytetune.common.event.song.SongUploadRequestEvent;
import com.maizi.bytetune.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongUploadRequestEventConsumer {

    private final SongService songService;

    @KafkaListener(topics = "${bytetune.messaging.topics.song-upload-request}", //
            groupId = "${bytetune.messaging.consumer.song-service.group-id}")
    public void consume(SongUploadRequestEvent event, Acknowledgment acknowledgment) {

        String filePath = event.getFilePath();
        try {

            // 1. 幂等检查
            if (songService.existsByFile(filePath, event.getMd5())) {
                log.info("\uD83D\uDCE9 Kafka 收到新消息 已存在：{}", filePath);
                acknowledgment.acknowledge();
                return;
            }
            log.info("\uD83D\uDCE9 Kafka 收到新消息 新增：{} ", filePath);
            // TODO
            // 2. 转换成 Song
            // Song song = SongEntityBuilder.fromEvent(event);

            // 3. 保存数据库
            // songService.save(song);

            // 4. 手动提交 Kafka offset
            acknowledgment.acknowledge();

        } catch (Exception e) {

            log.error("处理 SongUploadRequestEvent 失败，md5={}", event.getMd5(), e);
            // 不 ack
            // 交给 DefaultErrorHandler 重试 / DLQ
            throw e;
        }
    }
}