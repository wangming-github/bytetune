package com.bytetune.service;

import com.bytetune.dto.SongFileInfo;
import com.bytetune.entity.Song;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务类
 * </p>
 *
 * @author maizi
 * @since 2026-02-26
 */
public interface ISongService extends IService<Song> {

    void saveAll(List<Song> songs);

    List<Song> getAll();

    /**
     * 判断指定文件是否已经存在于数据库中
     *
     * <p>同时通过文件路径和 MD5 值判断，防止同名但内容不同的文件重复入库。</p>
     *
     * @param path 文件的绝对路径或相对路径
     * @param md5  文件的 MD5 值，用于唯一性判断
     * @return {@code true} 如果数据库中已存在该文件；{@code false} 如果不存在
     */
    boolean existsByFile(String path, String md5);

    List<Song> selectUnUploaded(int batchSize);

    void updateMinioStatus(Long id, int status, String bucketName, String objectName);

    /**
     * 第一次启动项目时扫描文件列表并批量入库数据库
     *
     * @param files     待处理的本地文件信息列表
     */
    void loadExistingSongs(List<SongFileInfo> files);
}
