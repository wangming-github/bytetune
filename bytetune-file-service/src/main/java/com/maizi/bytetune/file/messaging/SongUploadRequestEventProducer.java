package com.maizi.bytetune.file.messaging;

import com.maizi.bytetune.common.event.song.SongUploadRequestEvent;
import com.maizi.bytetune.file.config.FileServiceProperties;
import com.maizi.bytetune.messaging.publisher.Message;
import com.maizi.bytetune.messaging.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 歌曲上传请求事件生产者。
 *
 * <p>
 * 负责将文件模块产生的 {@link SongUploadRequestEvent}
 * 封装为通用 {@link Message}，并交由 {@link MessagePublisher}
 * 发布到消息中间件。
 * </p>
 *
 * <p>
 * 本类只负责：
 * <ul>
 *     <li>构建歌曲上传请求消息</li>
 *     <li>指定消息 Key</li>
 *     <li>指定消息 Topic</li>
 *     <li>调用通用消息发布器发送消息</li>
 * </ul>
 *
 * <p>
 * 不直接依赖 Kafka API，从而将文件业务与具体消息中间件实现解耦。
 */
@Component
@RequiredArgsConstructor
public class SongUploadRequestEventProducer {

    /**
     * 通用消息发布器。
     * <p>
     * 具体由 messaging 模块提供实现，例如 KafkaMessagePublisher。
     */
    private final MessagePublisher messagePublisher;

    /**
     * 文件服务配置。
     */
    private final FileServiceProperties fileServiceProperties;

    /**
     * 歌曲上传请求事件对应的消息 Topic。
     */
    @Value("${bytetune.messaging.topics.song-upload-request}")
    private String topic;

    /**
     * 发布单条歌曲上传请求事件。
     *
     * @param event 歌曲上传请求事件
     */
    public void publish(SongUploadRequestEvent event) {

        // 将业务事件封装为通用消息对象
        Message message = Message.builder()
                .key(event.getMd5())       // 使用文件 MD5 作为消息 Key，便于消息分区及后续幂等处理
                .payload(event)            // SongUploadRequestEvent 作为消息载荷
                .build();

        // 交由通用消息发布器发送，具体消息中间件由 MessagePublisher 实现决定
        messagePublisher.publish(topic, message);
    }

    /**
     * 批量发布歌曲上传请求事件。
     *
     * <p>
     * 将多个业务事件统一转换为通用 Message 后批量发送，
     * 减少上层业务与消息中间件实现之间的耦合。
     *
     * @param events 歌曲上传请求事件列表
     */
    public void publishBatch(List<SongUploadRequestEvent> events) {

        // 没有需要发送的事件时直接返回
        if (events == null || events.isEmpty()) {
            return;
        }

        // 将业务事件转换为通用消息对象
        List<Message> messages = events.stream()
                .map(event -> Message.builder()
                        .key(event.getMd5())       // 使用文件 MD5 作为消息 Key
                        .payload(event)             // SongUploadRequestEvent 作为消息载荷
                        .build())
                .toList();

        // 批量发送消息
        messagePublisher.publishBatch(topic, messages);
    }
}