package com.maizi.bytetune.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maizi.bytetune.common.constants.UploadStatus;
import com.maizi.bytetune.common.dto.SongUploadEventDTO;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.mapper.SongMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时扫描数据库中未上传的歌曲，并封装为 SongUploadEvent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SongUploadEventProducer {

    private final SongMapper songMapper;
    String topic = "song-upload-group";

    /**
     * Spring KafkaTemplate 注入
     * key: String（可用 songId 作为 key 保证顺序）
     * value: SongUploadEventDTO
     */
    private final KafkaTemplate<String, com.maizi.bytetune.common.dto.SongUploadEventDTO> kafkaTemplate;

    /**
     * 扫描周期：每30秒扫描一次（可根据需求调整）
     * <p>
     * TODO 方法过于杂乱 只做消息发布 和数据修改分开比较合适
     */
    @Scheduled(fixedDelay = 30_000)
    public void scanPendingSongs() {
        log.debug("开始扫描未上传的歌曲...");
        // 查询 status = 0 的歌曲（0=未上传）
        List<Song> pendingSongs = songMapper.selectList(new LambdaQueryWrapper<Song>() //
                .eq(Song::getStatus, UploadStatus.FAILED.getCode())//
                .or()//
                .eq(Song::getStatus, UploadStatus.NOT_UPLOADED));

        List<SongUploadEventDTO> events = songsToEventDTOS(pendingSongs);
        if (events == null) return;

        log.debug("扫描到 {} 条未上传歌曲，已封装成 SongUploadEvent。", events.size());
        events.forEach(event -> {
            kafkaTemplate.send(topic, event);
            log.info("已发送数据{}到 Kafka topic:{}", event.getSongName(), topic);
        });
        log.info("已发送{}条数据到 Kafka topic:{}", events.size(), topic);
    }

    @Nullable
    private static List<SongUploadEventDTO> songsToEventDTOS(List<Song> pendingSongs) {
        if (pendingSongs.isEmpty()) {
            log.info("没有未上传的歌曲。");
            return null;
        }

        return pendingSongs.stream()//
                .map(song -> SongUploadEventDTO.builder()//
                        .songId(song.getId())//
                        .songName(song.getName())//
                        .bucketName(song.getBucketName() != null ? song.getBucketName() : "songs")// TODO 使用配置类
                        .objectName(song.getObjectName())//
                        .filePath(song.getPath())//
                        .timestamp(System.currentTimeMillis())//
                        .retryCount(0).build()).toList();
    }

    // 启动
    // kafk_2 .13 - 4.2 .0 bin / kafka - server - start.sh config / server.properties
    // 发送测试消息a
    // ./bin/kafka-console-producer.sh --topic song-upload-group --bootstrap-server localhost:9092
    // 接收测试消息
    // ./bin/kafka-console-consumer.sh --topic song-upload-group --bootstrap-server localhost:9092 --from-beginning
}