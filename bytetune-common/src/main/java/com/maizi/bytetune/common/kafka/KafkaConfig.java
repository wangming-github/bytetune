package com.maizi.bytetune.common.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.swing.*;

/**
 * Kafka 消费者容器配置
 * 用于自定义 KafkaListener 线程名、并发数等参数
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaTemplate<String, KafkaSongEventDTO> kafkaTemplate;
    private final ConsumerFactory<String, KafkaSongEventDTO> consumerFactory;


    // 启动
    // kafk_2 .13 - 4.2 .0 bin / kafka - server - start.sh config / server.properties
    // 发送测试消息a
    // ./bin/kafka-console-producer.sh --topic song-upload-group --bootstrap-server localhost:9092
    // 接收测试消息
    // ./bin/kafka-console-consumer.sh --topic song-upload-group --bootstrap-server localhost:9092 --from-beginning

    /**
     * KafkaListener 容器工厂
     * 可用于 @KafkaListener 注入
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaSongEventDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaSongEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

       /*  setAckMode 【容器提交策略是否注入 Ack 对象】 【AckMode 是核心，决定 Spring 是否允许手动 ack】
           application-common.yml 【Kafka客户端默认提交行为】【enable-auto-commit 是客户端保险开关，防止 Kafka 自动提交 offset】 */
        // 设置手动提交 offset
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // 设置并发线程数
        factory.setConcurrency(1);

        // 设置容器属性
        factory.getContainerProperties().setPollTimeout(3000); // 每次 poll 超时时间

        // 配置 DLQ（死信队列）
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate), // 自动把处理失败的消息发送到指定 DLQ Topic
                new FixedBackOff(1000L, 3) // 重试 3 次，每次间隔 1s
        ));

        // 自定义线程名（日志中显示）
        factory.setContainerCustomizer(container -> {
            container.setBeanName("Kafka"); // 线程名前缀
        });

        return factory;
    }
}