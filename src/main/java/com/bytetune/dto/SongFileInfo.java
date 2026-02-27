package com.bytetune.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.Getter;
import lombok.ToString;

/**
 * 歌曲文件元信息
 *
 * <p>
 * 用于描述本地或上传文件解析后的基础信息。
 * 该对象仅在上传流程中使用，不参与数据库持久化。
 * </p>
 */
@Getter
@ToString
@Builder
public class SongFileInfo {

    /**
     * 原始文件名（如：yequ.mp3）
     */
    private final String fileName;

    /**
     * 文件绝对路径（仅本地文件上传时使用）
     */
    private final String absolutePath;

    /**
     * 文件大小（字节）
     */
    private final long size;

    /**
     * 音频时长（秒）
     */
    private final long duration;

    /**
     * 文件 MIME 类型（如 audio/mpeg）
     */
    private final String contentType;

    /**
     * 文件 MD5（用于防止重复上传）
     */
    private final String md5;

}