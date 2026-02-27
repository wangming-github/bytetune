package com.bytetune.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bytetune.entity.Song;
import com.bytetune.mapper.SongMapper;
import com.bytetune.service.ISongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author maizi
 * @since 2026-02-26
 */
@Service
@Slf4j
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {

    @Override
    @Transactional
    public void saveAll(List<Song> songs) {
        // this.saveBatch(songs);
        this.saveBatch(songs, 50); // 每?条单独执行 batch
    }

    @Override
    public List<Song> getAll() {
        return this.list();
    }

    @Override
    public boolean existsByFile(String path, String md5) {
        // 使用 LambdaQueryWrapper 避免硬编码列名
        long count = this.count(new LambdaQueryWrapper<Song>().eq(Song::getPath, path).eq(Song::getMd5, md5));
        return count > 0; // 大于 0 表示数据库已有该文件
    }

}
