package com.maizi.bytetune.messaging.publisher;

import java.util.List;

/**
 * 消息发布器。
 */
public interface MessagePublisher {

    /**
     * 发布消息。
     *
     * @param topic   Topic
     * @param message 消息
     */
    void publish(String topic, Message message);

    /**
     * 批量发布消息。
     *
     * @param topic    Topic
     * @param messages 消息
     */
    void publishBatch(String topic, List<Message> messages);
}