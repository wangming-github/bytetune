package com.maizi.bytetune.file;

import com.maizi.bytetune.common.dto.SongFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地歌曲文件扫描工具类
 *
 * <p>用于扫描指定文件夹下的音频文件，并获取文件的基本信息：
 * 文件名、路径、大小、音频时长、文件类型等。
 *
 * <p>可用于 ByteTune 项目本地歌曲导入、批量分析等功能。
 */
@Slf4j
public class AudioFileProcessor {
    /**
     * Tika 工具实例，用于解析音频文件，线程安全，可复用
     */
    private static final Tika tika = new Tika();

    /**
     * 扫描指定文件夹，获取所有音频文件的信息
     *
     * @param folderPath 音乐文件夹绝对路径
     * @return List<SongFileInfo> 包含所有音频文件信息的列表
     * @throws IOException 文件读取或解析异常
     */
    public static List<SongFileInfo> scan(String folderPath) throws Exception {
        List<SongFileInfo> songs = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("文件夹不存在或不是目录: " + folderPath);
        }

        File[] files = folder.listFiles();
        if (files == null) return songs;

        for (File file : files) {
            if (file.isFile()) {
                String mimeType = tika.detect(file);
                if (mimeType.startsWith("audio")) {
                    songs.add(getSongFileInfo(file));
                }
            }
        }
        return songs;
    }

    // 获取文件 MIME 类型
    public static String detectMimeType(File file) {
        try {
            return tika.detect(file);
        } catch (Exception e) {
            return "获取文件 unknown 类型";
        }
    }

    /**
     * 获取音频文件信息
     *
     * @param file 本地音频文件
     * @return SongFileInfo 文件信息对象
     * @throws Exception 文件读取或解析失败
     */
    public static SongFileInfo getSongFileInfo(File file) throws Exception {
        log.debug("开始解析文件: {}", file.getAbsolutePath());

        Metadata metadata = new Metadata();
        try (FileInputStream fis = new FileInputStream(file)) {
            tika.parse(fis, metadata);
        } catch (Exception e) {
            log.error("解析文件失败: {}", file.getAbsolutePath(), e);
            throw e;
        }

        // 解析音频时长（毫秒转换为秒）
        String durationStr = metadata.get("xmpDM:duration");
        int duration = 0;
        if (durationStr != null) {
            try {
                duration = (int) Double.parseDouble(durationStr);
            } catch (NumberFormatException e) {
                log.warn("文件时长解析失败: {}，使用默认值0", file.getName(), e);
            }
        }

        // 文件 MIME 类型
        String contentType = tika.detect(file);
        log.debug("文件类型: {}, 时长: {}秒", contentType, duration);

        // 计算文件 MD5
        String md5 = calcMD5(file);
        log.debug("文件 MD5: {}", md5);

        // 构建 SongFileInfo 对象
        SongFileInfo info = SongFileInfo.builder()//
                .fileName(file.getName()).absolutePath(file.getAbsolutePath()).size(file.length()).duration(duration)//
                .contentType(contentType).md5(md5).build();

        log.debug("解析完成: {}", info.getFileName());
        return info;
    }

    /**
     * 计算文件 MD5
     *
     * @param file 文件对象
     * @return MD5 字符串
     * @throws Exception IO 或算法异常
     */
    private static String calcMD5(File file) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        // 转换为16进制字符串
        return new java.math.BigInteger(1, md.digest()).toString(16);
    }

}