package com.maizi.bytetune.common.service.impl;

import com.maizi.bytetune.common.dto.SongFileInfo;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.service.SongExtService;
import com.maizi.bytetune.common.service.SongService;
import com.maizi.bytetune.common.util.SongEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SongExtServiceImpl implements SongExtService {

    @Autowired
    SongService songService;

    /**
     * 加载现有文件到数据库
     *
     * @param files 待处理的本地文件信息列表
     */
    @Override
    public void loadExistingSongs(List<SongFileInfo> files) {

        if (files == null || files.isEmpty()) {
            log.info("没有需要处理的文件！");
            return;
        }
        // 转换为数据库实体
        List<Song> songs = files.stream().map(SongEntityBuilder::toEntity).toList();
        log.info("现有的文件数量：{}", songs.size());
        // 过滤已经存在的歌曲（path + md5）
        List<Song> newSongs = songs.stream().filter(song -> !songService.existsByFile(song.getPath(), song.getMd5())).toList();
        log.info("过滤已经存在的歌曲，未入库数量：{}", newSongs.size());

        if (newSongs.isEmpty()) {
            log.info("无现有的文件数据需要导入！");
            return;
        }
        songService.saveAll(songs);
    }
}
