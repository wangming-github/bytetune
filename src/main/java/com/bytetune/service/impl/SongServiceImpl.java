package com.bytetune.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bytetune.dto.SongFileInfo;
import com.bytetune.entity.Song;
import com.bytetune.exception.BatchDuplicateException;
import com.bytetune.mapper.SongMapper;
import com.bytetune.service.ISongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bytetune.util.SongEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
        this.saveBatch(songs, 5); // 每?条单独执行 batch
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

    @Override
    public List<Song> selectUnUploaded(int batchSize) {
        return lambdaQuery().eq(Song::getStatus, 0).orderByAsc(Song::getId).page(new Page<>(1, batchSize)).getRecords();
    }

    @Override
    public void updateMinioStatus(Long id, int status, String bucketName, String objectName) {
        lambdaUpdate().eq(Song::getId, id).set(Song::getStatus, status).set(Song::getBucketName, bucketName).set(Song::getObjectName, objectName).update();
        log.info("Status更新完成!");
    }

    /**
     * 加载现有文件到数据库
     *
     * @param files 待处理的本地文件信息列表
     */
    @Override
    public void loadExistingSongs(List<SongFileInfo> files) {

        if (files == null || files.isEmpty()) {
            log.debug("没有需要处理的文件！");
            return;
        }
        // 转换为数据库实体
        List<Song> songs = files.stream().map(SongEntityBuilder::toEntity).toList();
        log.info("现有的文件数量：{}", songs.size());
        // 过滤已经存在的歌曲（path + md5）
        List<Song> newSongs = songs.stream().filter(song -> !existsByFile(song.getPath(), song.getMd5())).toList();
        log.info("过滤已经存在的歌曲，未入库数量：{}", newSongs.size());

        if (newSongs.isEmpty()) {
            log.info("无现有的文件数据需要导入！");
            return;
        }
        saveAll(songs);
    }

}
