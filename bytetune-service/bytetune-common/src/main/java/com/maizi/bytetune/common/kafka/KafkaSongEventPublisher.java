package com.maizi.bytetune.common.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kafka 事件发布服务
 * <p>
 * 职责：
 * 1. 负责将事件发送到指定 Topic
 * 2. 不关心数据来源
 * <p>
 * 注意：
 * - 建议使用 songId 作为 key，保证同一歌曲顺序性
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSongEventPublisher {

    private final KafkaTemplate<String, KafkaSongEventDTO> kafkaTemplate;

    @Value("${bytetune.kafka.topic.name}")
    private String topic;

    /**
     * 批量发布上传事件
     *
     * @param events 待发送的事件列表
     */
    public void publishBatch(List<KafkaSongEventDTO> events) {
        events.forEach(event -> {
            // 使用 songId 作为 key，保证同一歌曲消息顺序
            // kafkaTemplate.send("topic", partition, key, value);
            kafkaTemplate.send(topic, event.getSongId().toString(), event)//
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("发送 Kafka 消息:[{}],发送成功", event.getSongName());
                        } else {
                            log.warn("发送 Kafka 消息:[{}],发送失败", event.getSongName());
                        }
                    });

        });
    }
}