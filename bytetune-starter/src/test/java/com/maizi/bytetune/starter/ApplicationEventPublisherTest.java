package com.maizi.bytetune.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
public class ApplicationEventPublisherTest {

    // 解耦模块之间的调用关系。不是 MQ，不保证可靠投递，不跨进程。⚙️
    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void testPublishEvent() throws InterruptedException {
        System.out.println("发布事件前");

        SongUploadEvent event = new SongUploadEvent(123L, "/tmp/test.mp3");
        publisher.publishEvent(event);

        System.out.println("事件已发布");

        // 等待异步处理完成（如果 B 的 @EventListener 是 @Async）
        Thread.sleep(2000);
    }
}