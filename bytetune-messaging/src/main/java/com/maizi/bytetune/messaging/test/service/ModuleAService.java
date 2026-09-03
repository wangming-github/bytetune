package com.maizi.bytetune.messaging.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModuleAService {

    private final ApplicationEventPublisher publisher;

    public void uploadSong(Long songId, String filePath) {
        // TODO 测试
        // // SongUploadedEvent event = new SongUploadedEvent(songId, filePath);
        // publisher.publishEvent(event);  // 发布事件
        // System.out.println("Module A 发布事件: " + event);
    }
}