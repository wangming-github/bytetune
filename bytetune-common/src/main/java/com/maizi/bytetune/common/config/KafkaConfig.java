package com.maizi.bytetune.common.config;

import com.maizi.bytetune.common.dto.SongUploadEventDTO;
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

    private final ConsumerFactory<String, SongUploadEventDTO> consumerFactory;

    /**
     * KafkaListener 容器工厂
     * 可用于 @KafkaListener 注入
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SongUploadEventDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SongUploadEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();

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