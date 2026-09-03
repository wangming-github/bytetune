package com.maizi.bytetune.file.util;

import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.file.model.SongFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 文件信息转换工具
 *
 * <p>将 SongFileInfo 转换为 Song Entity，方便入库。</p>
 */
@Slf4j
public class SongEventBuilder {

    public static List<FileToSongEventDto> toSongEventList(List<SongFileInfo> files) {

        if (files == null || files.isEmpty()) {
            log.info("没有需要处理的文件！");
            return Collections.emptyList();
        }

        return files.stream().map(SongEventBuilder::toEvent).toList();
    }

    /**
     * 将本地解析的 SongFileInfo 转换为SongUploadedEvent事件对象
     *
     * <p>
     * 注意：
     * - MinIO 上传信息暂时为空，上传成功后需要填充 bucketName 和 objectName
     * - 上传状态初始化为 NOT_UPLOADED
     * </p>
     *
     * @param f 本地解析的文件信息
     * @return 对应的 Song 实体
     */

    public static FileToSongEventDto toEvent(SongFileInfo f) {
        LocalDateTime now = LocalDateTime.now(); // 事件创建时间
        return FileToSongEventDto.builder().songName(f.getName()) // 歌曲文件名
                .filePath(f.getPath()) // 本地文件绝对路径
                .contentType(f.getContentType()) // 文件 MIME 类型，例如 audio/mpeg
                .size(f.getSize()) // 文件大小，单位：字节
                .duration((int) f.getDuration()) // 音频时长，单位：秒

                .md5(f.getMd5()) // 文件 MD5，用于文件去重和完整性校验

                .bucketName(null) // MinIO 上传前暂无 Bucket 信息
                .objectName(null) // MinIO 上传前暂无 Object 信息
                .coverBucket(null) // 封面上传前暂无 Bucket 信息
                .coverObject(null) // 封面上传前暂无 Object 信息
                .build();
    }

}
