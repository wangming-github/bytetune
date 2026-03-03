package com.maizi.bytetune.common.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * Kafka 消费者容器配置
 * 用于自定义 KafkaListener 线程名、并发数等参数
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

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

        // 设置并发线程数
        factory.setConcurrency(1);

        // 设置容器属性
        factory.getContainerProperties().setPollTimeout(3000); // 每次 poll 超时时间

        // 自定义线程名（日志中显示）
        factory.setContainerCustomizer(container -> {
            container.setBeanName("Kafka"); // 线程名前缀
        });

        return factory;
    }
}