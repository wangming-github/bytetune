package com.maizi.bytetune.messaging.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka 消费者容器配置
 *
 * <p>
 * 负责：
 * <ul>
 *     <li>KafkaListener 容器配置</li>
 *     <li>手动提交 offset</li>
 *     <li>消费者并发数</li>
 *     <li>消费者线程名称</li>
 *     <li>消费异常重试</li>
 *     <li>失败消息进入 DLT</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final ConsumerFactory<String, Object> consumerFactory;

    /**
     * KafkaListener 容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(DefaultErrorHandler kafkaErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        // Kafka ConsumerFactory
        factory.setConsumerFactory(consumerFactory);

        /**
         * 手动提交 offset
         *
         * 消费成功后，由业务代码调用：
         *
         * acknowledgment.acknowledge();
         */
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        /**
         * KafkaListener 并发消费者数量
         */
        factory.setConcurrency(1);

        /**
         * poll 超时时间
         */
        factory.getContainerProperties().setPollTimeout(3000);

        /**
         * Kafka 消费异常处理器
         */
        factory.setCommonErrorHandler(kafkaErrorHandler);

        /**
         * 自定义 Kafka 容器 Bean 名称
         */
        factory.setContainerCustomizer(container -> {
            container.setBeanName("Kafka");
        });

        return factory;
    }

    /**
     * 创建 DLT 消息恢复器
     *
     * <p>
     * 消费失败并且重试耗尽后：
     * <p>
     * 原 Topic
     * ↓
     * 原 Topic + ".DLT"
     * <p>
     * 例如：
     * <p>
     * song-to-storage
     * ↓
     * song-to-storage.DLT
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {

        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
    }

    /**
     * Kafka 消费异常处理器
     *
     * <p>
     * 消费失败后：
     * <p>
     * 第一次消费
     * ↓
     * 失败
     * ↓
     * 等待 1 秒
     * ↓
     * 第一次重试
     * ↓
     * 仍然失败
     * ↓
     * 发送 DLT
     */
   /*  @Bean
    public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {

        FixedBackOff backOff = new FixedBackOff(1000L, 1L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
 */

    /**
     * Kafka 消费异常处理器。
     *
     * <p>失败后每隔 1 秒重试一次，最多重试 3 次。</p>
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {

        FixedBackOff backOff = new FixedBackOff(1000L, // 重试间隔 1 秒
                3L     // 最大重试 3 次
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        /*
         * RetryListener：
         * 监听 DefaultErrorHandler 的重试过程。
         */
        errorHandler.setRetryListeners(new RetryListener() {

            /**
             * 每次消费失败都会进入这里。
             *
             * @param record 当前失败的 Kafka 消息
             * @param exception 异常
             * @param deliveryAttempt 当前尝试次数
             */
            @Override
            public void failedDelivery(ConsumerRecord<?, ?> record, Exception exception, int deliveryAttempt) {
                System.out.printf("🔄 Kafka 消费失败，准备重试：topic=%s, partition=%d, offset=%d, attempt=%d, exception=%s, message=%s%n", record.topic(), record.partition(), record.offset(), deliveryAttempt, exception.getClass().getSimpleName(), exception.getMessage());
            }

            /**
             * 消息最终恢复成功。
             */
            @Override
            public void recovered(ConsumerRecord<?, ?> record, Exception exception) {

                System.out.printf("✅ Kafka 消息恢复成功：topic=%s, partition=%d, offset=%d%n", record.topic(), record.partition(), record.offset());
            }

            /**
             * DLT 恢复失败。
             */
            @Override
            public void recoveryFailed(ConsumerRecord<?, ?> record, Exception original, Exception failure) {

                System.out.printf("❌ Kafka DLT 处理失败：topic=%s, partition=%d, offset=%d, original=%s, failure=%s%n", record.topic(), record.partition(), record.offset(), original.getMessage(), failure.getMessage());
            }
        });

        return errorHandler;
    }
}