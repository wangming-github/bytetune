package com.maizi.bytetune.messaging;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/*
                     Kafka
                       │
                       ▼
             ErrorHandlingDeserializer
                       │
                       ▼
                JsonDeserializer
                       │
             ┌─────────┴─────────┐
             │                   │
           成功                  失败
             │                   │
             ▼                   ▼
       @KafkaListener       DefaultErrorHandler
             │                   │
             │             retry × 3
             │                   │
             │                   ▼
             │        DeadLetterPublishingRecoverer
             │                   │
             │                   ▼
             │             xxx.DLT
             │
             ▼
        业务处理成功
             │
             ▼
       acknowledgment
 */
@Configuration
public class KafkaErrorHandlerConfig {

    /**
     * 创建死信消息恢复器。
     *
     * <p>当消息经过重试仍然处理失败时，
     * 将消息发送到原 Topic 对应的 DLT Topic。</p>
     *
     * <p>例如：</p>
     * <pre>
     * file-to-song
     *      ↓
     * file-to-song.DLT
     *
     * song-to-storage
     *      ↓
     * song-to-storage.DLT
     * </pre>
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(//
            KafkaTemplate<String, Object> kafkaTemplate) {//

        return new DeadLetterPublishingRecoverer(//
                kafkaTemplate,//
                (record, exception) -> new TopicPartition(//
                        record.topic() + ".DLT", // 原 Topic 后追加 .DLT
                        record.partition()        // 保持原分区
                )
        );
    }

    /**
     * Kafka 消费异常处理器。
     *
     * <p>消息消费失败后最多重试 3 次，
     * 每次重试间隔 1 秒。</p>
     *
     * <p>3 次重试仍然失败后，
     * 消息会被发送到 DLT。</p>
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {

        FixedBackOff backOff = new FixedBackOff(1000L, // 重试间隔：1 秒
                3L     // 最大重试次数：3 次
        );

        return new DefaultErrorHandler(recoverer, // 重试失败后交给 DLT
                backOff   // 重试策略
        );
    }
}