package com.maizi.bytetune.starter;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ModuleBService {

    @EventListener
    public void handleUploadA(SongUploadEvent event) {
        System.out.println("Module B 收到事件: " + event);
        // 处理上传逻辑
    }

    @Async
    @EventListener
    public void handleUploadB(SongUploadEvent event) {
        // 异步处理，不阻塞发布者
        System.out.println("Module B 异步处理事件: " + event);
    }
}