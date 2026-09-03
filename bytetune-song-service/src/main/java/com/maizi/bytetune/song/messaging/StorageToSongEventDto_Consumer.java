package com.maizi.bytetune.song.messaging;

import com.maizi.bytetune.common.constants.UploadStatusCode;
import com.maizi.bytetune.contract.event.storage.StorageToSongEventDto;
import com.maizi.bytetune.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageToSongEventDto_Consumer {

    private final SongService songService;

    /*
         storage-service
                │
                │ 上传 MinIO
                ▼
              MinIO
                │
                │ StorageToSongEventDto
                ▼
              Kafka
                │
                ▼
           song-service
                │
                ▼
      更新 bucketName/objectName
     */
    @KafkaListener(//
            topics = "${bytetune.messaging.topics.storage-to-song}", //
            groupId = "${bytetune.messaging.consumer.song-service.group-id}")
    public void consume(StorageToSongEventDto event, Acknowledgment acknowledgment, ConsumerRecord<String, StorageToSongEventDto> record) {
        log.info("📩Kafka收到【文件服务器存储完成，返回Bucket相关】的消息。topic={}, event={}", record.topic(), event);

        songService.updateStorageInfo(//
                event.getSongId(),//
                UploadStatusCode.SUCCESS.getCode(),//
                event.getBucketName(), //
                event.getObjectName());//

        acknowledgment.acknowledge();
        log.info("文件【{}】的Bucket、ObjectName信息更新完成.", event.getObjectName());
    }
}