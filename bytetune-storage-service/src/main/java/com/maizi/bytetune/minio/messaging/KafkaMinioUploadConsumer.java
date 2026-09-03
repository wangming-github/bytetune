package com.maizi.bytetune.minio.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Kafka 消费者服务
 * 负责消费 song-upload-group Topic 中的 SongUploadEventDTO 消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMinioUploadConsumer {

    // private final MinioService minioService;
    // private final SongMapper songMapper;
    //
    // /**
    //  * 监听 Kafka Topic: "song-upload-group"
    //  * groupId 可以指定消费组 ID
    //  *
    //  * @param event 消息对象
    //  */
    // @KafkaListener(topics = "${bytetune.kafka.topic.name}")// groupId已经全局配置
    // public void consume(KafkaSongEventDTO event, Acknowledgment ack) {
    //     MDC.put("job", "[消费消息→上传文件→修改状态]");
    //     // 打印消息
    //     log.info("收到 Kafka 消息 ObjectName: {}", event.getSongName());
    //     Song song = songMapper.selectById(event.getSongId());
    //     log.debug("mysql查询该数据信息：{}", song.toString());
    //     // ✅ 幂等判断
    //     if (UploadStatusCode.SUCCESS.getCode() == (song.getStatus())) {
    //         log.info("已上传成功，跳过 id={}", song.getId());
    //         return;
    //     }
    //     if (minioService.uploadToMinioAndUpdateState(song)) {
    //         ack.acknowledge();// 成功后手动提交 offset
    //         log.info("消息已处理并提交 offset:[{}]", event.getSongName());
    //     } else {
    //         log.warn("上传/修改状态，失败。稍后重试...");
    //         // 这里不调用 ack.acknowledge()，Kafka 会保留 offset，下次重新消费
    //     }
    // }
}