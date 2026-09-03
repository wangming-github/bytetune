package com.maizi.bytetune.messaging.publisher;

import lombok.Builder;
import lombok.Getter;

/**
 * 通用消息。
 */
@Getter
@Builder
public class Message {

    /**
     * 消息 Key。
     */
    private final String key;

    /**
     * 消息内容。
     */
    private final Object payload;
}