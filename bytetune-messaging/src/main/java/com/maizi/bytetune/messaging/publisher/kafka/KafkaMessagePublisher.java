package com.maizi.bytetune.messaging.publisher.kafka;

import com.maizi.bytetune.messaging.publisher.Message;
import com.maizi.bytetune.messaging.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kafka 消息发布器。
 *
 * <p>
 * 负责将通用消息发送到 Kafka。
 * 不关心具体业务事件类型，也不关心消息来源。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher implements MessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发布消息。
     *
     * @param topic   Kafka Topic
     * @param message 消息
     */
    @Override
    public void publish(String topic, Message message) {

        kafkaTemplate.send(topic, message.getKey(), message.getPayload())//
                .whenComplete((result, ex) -> {

                    if (ex == null) {
                        log.info("\uD83D\uDCE4 Kafka 消息发送成功，topic={}, key={}", topic, message.getKey());
                    } else {
                        log.error("\uD83D\uDCE4 Kafka 消息发送失败，topic={}, key={}", topic, message.getKey(), ex);
                    }
                });
    }

    /**
     * 批量发布消息。
     *
     * @param topic    Kafka Topic
     * @param messages 消息列表
     */
    @Override
    public void publishBatch(String topic, List<Message> messages) {

        messages.forEach(message -> publish(topic, message));
    }
}