package com.maizi.bytetune.starter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModuleAService {

    private final ApplicationEventPublisher publisher;

    public void uploadSong(Long songId, String filePath) {
        SongUploadEvent event = new SongUploadEvent(songId, filePath);
        publisher.publishEvent(event);  // 发布事件
        System.out.println("Module A 发布事件: " + event);
    }
}