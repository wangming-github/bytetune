package com.maizi.bytetune.minio.messaging;

import com.maizi.bytetune.common.util.FileNameUtils;
import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import com.maizi.bytetune.contract.event.storage.StorageToSongEventDto;
import com.maizi.bytetune.minio.service.MinioService;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/*
                     Kafka
                       │
            song-to-storage
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   Partition 0    Partition 1    Partition 2
        │              │              │
        └──────────────┼──────────────┘
                       ▼
              Storage Consumer
                       │
                  幂等检查
                       │
                ┌──────┴──────┐
                │             │
              成功            失败
                │             │
                ▼             ▼
              MinIO        Retry Topic
                │             │
                │        指数退避
                │             │
                │        ┌────┴────┐
                │        │         │
                │      成功       失败
                │        │         │
                │       ACK       DLT
                │                  │
                └──────────────────┘
 */
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
            groupId = "${bytetune.messaging.consumer.storage-service.group-id}")
    public void consume(SongToStorageEventDto event, //
                        Acknowledgment acknowledgment, //
                        ConsumerRecord<String, SongToStorageEventDto> record) {
        String path = event.getPath();
        String fileName = FileNameUtils.getFileName(path);
        log.info("📩Kafka收到【文件请求上传服务器】的消息。文件=【{}】topic=【{}】", fileName, record.topic());

        ObjectWriteResponse response = minioService.uploadToMinio(event, fileName);// 文件上传至服务器

        producer.publish(buildStorageToSongEvent(event, response));// 服务器返回结果封装的消息载体

        log.info("返回信息已经发送。文件=【{}】topic=【{}】", fileName, record.topic());
        // ===== DLT 测试 =====
        //  throw new RuntimeException("模拟 MinIO 上传失败");
        acknowledgment.acknowledge();// 发送消息（ 实际上 Kafka 的 ACK 是提交消费 offset，不是业务成功确认。）
    }

    @KafkaListener(//
            topics = "${bytetune.messaging.topics.song-to-storage}.DLT",//
            groupId = "bytetune-storage-dlt-group")
    public void consumeDLT(SongToStorageEventDto event, Acknowledgment acknowledgment, ConsumerRecord<String, Object> record) {
        log.error("☠️ Kafka DLT 消息：文件=【{}】topic={}, partition={}, offset={}, key={}, value={}", event.getObjectName(), record.topic(), record.partition(), record.offset(), record.key(), record.value());
        acknowledgment.acknowledge();
        // TODO:
        // 1. 保存失败记录
        // 2. 记录失败原因
        // 3. 发送告警
        // 4. 后台管理页面展示
        // 5. 提供人工重新处理能力
    }

    /**
     * 构造文件存储完成事件。
     *
     * @param event    歌曲文件上传事件
     * @param response MinIO 文件上传响应
     * @return 存储完成事件
     */
    private StorageToSongEventDto buildStorageToSongEvent(SongToStorageEventDto event, ObjectWriteResponse response) {
        return StorageToSongEventDto.builder().songId(event.getId()).md5(event.getMd5()).bucketName(response.bucket()).objectName(response.object()).contentType(event.getContentType()).build();
    }

}