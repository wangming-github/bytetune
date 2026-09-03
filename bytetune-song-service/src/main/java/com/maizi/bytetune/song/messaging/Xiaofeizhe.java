package com.maizi.bytetune.song.messaging;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;

public class Xiaofeizhe {

    // TODO  获取到消息SongUploadRequestEvent 在进行后续处理

    // /**
    //  * 批量入库缓存中的歌曲
    //  */
    // public void flushBuffer() {
    //     if (songList.isEmpty()) return;
    //     songService.saveAll(new ArrayList<>(songList));
    //     log.debug("批量缓存入库完成，数量：{}", songList.size());
    //     songList.clear();
    // }
    //
    // /**
    //  * 每 ? 秒强制提交
    //  * OR
    //  * LinkedBlockingQueue + 单线程消费线程
    //  */
    // @Scheduled(fixedDelay = 10_000)
    // public void autoFlush() {
    //     synchronized (this) {
    //         if (!songList.isEmpty()) {
    //             log.info("定时提交触发，保存当前缓存，数量：{}", songList.size());
    //             flushBuffer();
    //         }
    //     }
    // }

}
