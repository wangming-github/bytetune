package com.bytetune.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 歌曲文件信息封装类
 *
 * <p>存储扫描到的单个音频文件信息
 */
@Data
@AllArgsConstructor
public class SongFileInfo {
    /**
     * 歌曲文件名
     */
    private final String name;
    /**
     * 文件绝对路径
     */
    private final String path;
    /**
     * 文件大小（字节）
     */
    private final long size;
    /**
     * 音频时长（秒）
     */
    private final int duration;
    /**
     * 文件 MIME 类型
     */
    private final String fileType;
}