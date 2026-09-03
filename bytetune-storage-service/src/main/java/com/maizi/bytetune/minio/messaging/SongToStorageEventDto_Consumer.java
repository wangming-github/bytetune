package com.maizi.bytetune.minio.messaging;

import com.maizi.bytetune.common.util.FileNameUtils;
import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import com.maizi.bytetune.contract.event.storage.StorageToSongEventDto;
import com.maizi.bytetune.minio.service.MinioService;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongToStorageEventDto_Consumer {

    private final MinioService minioService;
    private final StorageToSongEventDto_Producer producer;

    /*
           song-service
                │
                │
                ▼
            MySQL Song
                │
                │ SongToStorageEventDto
                ▼
              Kafka
                │
                ▼
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
            topics = "${bytetune.messaging.topics.song-to-storage}", //
            groupId = "${bytetune.messaging.consumer.song-service.group-id}")
    public void consume(SongToStorageEventDto event, //
                        Acknowledgment acknowledgment, //
                        ConsumerRecord<String, SongToStorageEventDto> record) {
        String path = event.getPath();
        String fileName = FileNameUtils.getFileName(path);
        log.info("📩Kafka收到【文件请求上传服务器】的消息。文件=【{}】topic=【{}】", fileName, record.topic());

        ObjectWriteResponse response = minioService.uploadToMinio(event, fileName);
        StorageToSongEventDto backEvent = StorageToSongEventDto.builder()
                .songId(event.getId()).md5(event.getMd5())
                .bucketName(response.bucket())
                .objectName(response.object())
                .contentType(event.getContentType())
                .build();

        producer.publish(backEvent);

        acknowledgment.acknowledge();

        log.info("返回信息已经发送。文件=【{}】topic=【{}】", fileName, record.topic());
    }
}