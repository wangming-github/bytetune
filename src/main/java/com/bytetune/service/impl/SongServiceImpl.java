package com.bytetune.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bytetune.entity.Song;
import com.bytetune.mapper.SongMapper;
import com.bytetune.service.ISongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {

    @Override
    @Transactional
    public void saveAll(List<Song> songs) {
        this.saveBatch(songs);
    }

    @Override
    public List<Song> getAll() {
        return this.list();
    }

    /**
     * 判断数据库中是否存在指定 file_url 的歌曲
     *
     * @param fileUrl 文件完整路径或 URL
     * @return true 已存在，false 不存在
     */
    @Override
    public boolean existsByFileUrl(String fileUrl) {
        // 使用 LambdaQueryWrapper 更安全，避免硬编码列名
        long count = this.count(
                new LambdaQueryWrapper<Song>().eq(Song::getFileUrl, fileUrl));
        return count > 0; // 大于 0 表示数据库已有
    }

}
