package com.maizi.bytetune.common.kafka;

import com.maizi.bytetune.common.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private final KafkaSongEventPublisher eventPublisher;

    /**
     * 每 30 秒执行一次扫描
     * fixedDelay：上一次执行完成后延迟 ??_000 秒再执行
     */
    @Scheduled(fixedDelay = 60_000)
    public void scanPendingSongs() {

        log.debug("开始扫描未上传歌曲...");
        // 查询并封装为事件对象
        List<KafkaSongEventDTO> events = songService.loadPendingUploadEvents();
        if (events.isEmpty()) {
            log.debug("未发现需要上传的歌曲");
            return;
        }
        log.info("扫描到{}条没有上传的数据。", events.size());
        // 批量发送消息
        eventPublisher.publishBatch(events);

    }
}