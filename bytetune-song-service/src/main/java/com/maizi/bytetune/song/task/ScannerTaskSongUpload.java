package com.maizi.bytetune.song.task;

import com.maizi.bytetune.messaging.publisher.MessagePublisher;
import com.maizi.bytetune.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 消费者高级用法 1.1 并发消费 1.2 消费幂等 + 事务控制 1.3 死信队列（DLQ） 1.4 手动提交 offset
// 生产者高级用法 2.1 顺序保证 2.2 异步回调 & 重试 2.3 事务生产者 2.4 Headers 与 Metadata

/**
 * 定时扫描数据库中未上传的歌曲，并封装为 SongUploadEvent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScannerTaskSongUpload {

    private final SongService songService;
    private final MessagePublisher eventPublisher;

    /**
     * 每 30 秒执行一次扫描
     * fixedDelay：上一次执行完成后延迟 ??_000 秒再执行
     */
    @Scheduled(fixedDelay = 60_000)
    public void scanPendingSongs() {

        // TODO
        // log.debug("开始扫描未上传歌曲...");
        // // 查询并封装为事件对象
        // List<SongUploadRequestEvent> events = songService.loadPendingUploadEvents();
        // if (events.isEmpty()) {
        //     log.debug("未发现需要上传的歌曲");
        //     return;
        // }
        //
        // // 批量发送消息
        // // TODO 去处对SongUploadedEvent的依赖改用同目录其他方式
        // eventPublisher.publishBatch(events);
    }

}