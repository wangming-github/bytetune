package com.maizi.bytetune.song.messaging;

import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import com.maizi.bytetune.messaging.publisher.Message;
import com.maizi.bytetune.messaging.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 歌曲上传请求事件生产者。
 *
 * <p>
 * 负责将文件模块产生的 {@link FileToSongEventDto}
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
@Slf4j
@Component
@RequiredArgsConstructor
public class SongToStorageEventDto_Producer {

    @Value("${bytetune.messaging.topics.song-to-storage}")
    private String topic;
    private final MessagePublisher messagePublisher;

    /**
     * 发布单条歌曲上传请求事件。
     *
     * @param event 歌曲上传请求事件
     */
    public void publish(SongToStorageEventDto event) {

        // 将业务事件封装为通用消息对象
        Message message = Message.builder()//
                .key(event.getMd5())       // 使用文件 MD5 作为消息 Key，便于消息分区及后续幂等处理
                .payload(event)            // SongUploadRequestEvent 作为消息载荷
                .build();

        // 交由通用消息发布器发送，具体消息中间件由 MessagePublisher 实现决定
        messagePublisher.publish(topic, message);
    }

    /**
     * 批量发布文件上传文件服务器事件。
     *
     * <p>
     * 将多个业务事件统一转换为通用 Message 后批量发送，
     * 减少上层业务与消息中间件实现之间的耦合。
     *
     * @param events 文件上传文件服务器请求事件列表
     */
    public void publishBatch(List<SongToStorageEventDto> events) {

        // 没有需要发送的事件时直接返回
        if (events == null || events.isEmpty()) {
            return;
        }

        // 将业务事件转换为通用消息对象
        List<Message> messages = events.stream()//
                .map(event -> Message.builder()//
                        .key(event.getMd5())       // 使用文件 MD5 作为消息 Key
                        .payload(event)             // SongUploadRequestEvent 作为消息载荷
                        .build()).toList();

        // 批量发送消息
        messagePublisher.publishBatch(topic, messages);
    }
}