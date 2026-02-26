package com.bytetune.util;

import com.bytetune.dto.SongFileInfo;
import com.bytetune.entity.Song;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SongFileScanner 扩展方法：把扫描到的文件信息转换成 Song 实体
 */
public class SongFileScannerExt {

    public static List<Song> toEntityList(List<SongFileInfo> files) {
        return files.stream().map(f -> {
            Song song = new Song();
            song.setName(f.getName());
            song.setFileUrl(f.getPath());  // 可以根据需求改成 MinIO URL
            song.setFileType(f.getFileType());
            song.setDuration(f.getDuration());
            return song;
        }).collect(Collectors.toList());
    }

    public static Song toEntity(SongFileInfo f) {
        Song song = new Song();
        song.setName(f.getName());
        song.setFileUrl(f.getPath());  // 可以根据需求改成 MinIO URL
        song.setFileType(f.getFileType());
        song.setDuration(f.getDuration());
        return song;
    }
}