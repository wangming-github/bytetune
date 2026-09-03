package com.maizi.bytetune.song.messaging;

import com.maizi.bytetune.common.util.FileNameUtils;
import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import com.maizi.bytetune.contract.event.storage.StorageToSongEventDto;
import com.maizi.bytetune.song.entity.Song;
import com.maizi.bytetune.song.service.SongService;
import com.maizi.bytetune.song.util.SongEntityBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileToSongEventDto_Consumer {

    private final SongService songService;
    private final SongToStorageEventDto_Producer songToStorageEventProducer;

    /*
               file-service
                    │
                    │ FileToSongEventDto
                    ▼
                  Kafka
                    │
                    ▼
               song-service
                    │
                    │ 创建 Song
                    ▼
                MySQL Song
     */
    @KafkaListener(topics = "${bytetune.messaging.topics.file-to-song}", //
            groupId = "${bytetune.messaging.consumer.storage-service.group-id}")
    public void consume(FileToSongEventDto event, Acknowledgment acknowledgment, ConsumerRecord<String, FileToSongEventDto> record) {

        String filePath = event.getFilePath();
        String name = FileNameUtils.getFileNameWithoutExtension(event.getFilePath());
        // 幂等检查
        if (songService.existsByFile(filePath, event.getMd5())) {
            log.info("📩Kafka收到【已记录】【扫描的文件信息记录至Mysql】的消息。文件=【{}】,topic=【{}】。", name, record.topic());
            acknowledgment.acknowledge();
            return;
        }
        try {
            log.info("📩Kafka收到【未记录】【扫描的文件信息记录至Mysql】的消息。文件=【{}】,topic=【{}】。", name, record.topic());
            // 1. 转换事件为数据库实体
            Song song = SongEntityBuilder.fromEvent(event);
            // 2. 保存数据库
            songService.save(song);
            // 3. 保存成功后，发送文件上传事件
            songToStorageEventProducer.publish(builder(song));
            // 4. 所有业务处理成功后，再 ACK Kafka
            acknowledgment.acknowledge();
            log.info("歌曲【{}】信息存储 MySQL，并发送存储文件消息完成.", name);
        } catch (Exception e) {
            log.error("处理 SongUploadRequestEvent 失败，md5={}", event.getMd5(), e);
            // 不 ack 交给 DefaultErrorHandler 重试 / DLQ
            throw e;
        }
    }

    /**
     * BeanUtils.copyProperties(A,B) 是“同名属性复制” A TO B
     * song to SongToStorageEventDto
     */
    private static SongToStorageEventDto builder(Song song) {
        SongToStorageEventDto dto = new SongToStorageEventDto();
        BeanUtils.copyProperties(song, dto);
        return dto;
    }
}