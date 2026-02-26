package com.bytetune.service;

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
     * 判断数据库中是否存在指定 file_url 的歌曲
     *
     * @param fileUrl 文件完整路径或 URL
     * @return true 已存在，false 不存在
     */
    boolean existsByFileUrl(String fileUrl);
}
