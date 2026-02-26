package com.bytetune.util;

import com.bytetune.dto.SongFileInfo;
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
public class SongFileScanner {

    /**
     * Tika 工具实例，用于解析音频文件
     */
    private static final Tika tika = new Tika();

    /**
     * 扫描指定文件夹，获取所有音频文件的信息
     *
     * @param folderPath 音乐文件夹绝对路径
     * @return List<SongFileInfo> 包含所有音频文件信息的列表
     * @throws IOException 文件读取或解析异常
     */
    public static List<SongFileInfo> scanFolder(String folderPath) throws Exception {
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
            return "unknown";
        }
    }

    /**
     * 获取单个音频文件的信息
     *
     * @param file 音频文件对象
     * @return SongFileInfo 音频文件信息对象
     * @throws IOException 文件读取或解析异常
     */
    // 单文件获取 SongFileInfo
    public static SongFileInfo getSongFileInfo(File file) throws Exception {
        Metadata metadata = new Metadata();
        try (FileInputStream fis = new FileInputStream(file)) {
            tika.parse(fis, metadata);
        }
        String durationStr = metadata.get("xmpDM:duration");
        int duration = 0;
        if (durationStr != null) duration = (int) (Double.parseDouble(durationStr) / 1000);
        return new SongFileInfo(file.getName(), file.getAbsolutePath(), file.length(), duration, detectMimeType(file));
    }

}