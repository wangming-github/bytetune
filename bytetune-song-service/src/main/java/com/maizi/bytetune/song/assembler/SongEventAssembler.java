package com.maizi.bytetune.song.assembler;

import com.maizi.bytetune.common.event.song.SongUploadRequestEvent;
import com.maizi.bytetune.song.entity.Song;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件转换器
 * <p>
 * 职责：
 * 将数据库实体 Song 转换为 SongUploadedEvent
 * <p>
 * 说明：
 * 只做对象转换，不做业务逻辑
 */
@Component
public class SongEventAssembler {

    /**
     * 批量转换实体为事件对象
     */
    public List<SongUploadRequestEvent> toEvents(List<Song> songs) {
        return songs.stream()//
                .map(song -> SongUploadRequestEvent.builder()//
                        .songId(song.getId())//
                        .songName(song.getName())//
                        .build())//
                .toList();
    }

}