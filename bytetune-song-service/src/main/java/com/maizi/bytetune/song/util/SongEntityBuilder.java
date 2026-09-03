package com.maizi.bytetune.song.util;

import com.maizi.bytetune.common.constants.UploadStatusCode;
import com.maizi.bytetune.common.dto.SongFileInfo_bak;
import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.song.entity.Song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件信息转换工具
 *
 * <p>将 SongFileInfo 转换为 Song Entity，方便入库。</p>
 */
public class SongEntityBuilder {

    /**
     * 批量转换
     *
     * @param files SongFileInfo 列表
     * @return Song Entity 列表
     */
    public static List<Song> toEntityList(List<SongFileInfo_bak> files) {
        return files.stream().map(SongEntityBuilder::toEntity).collect(Collectors.toList());
    }

    /**
     * 将本地解析的 SongFileInfo 转换为数据库持久化的 Song 实体
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
    public static Song toEntity(SongFileInfo_bak f) {
        LocalDateTime now = LocalDateTime.now();
        //  基础文件信息
        return Song.builder()                       //
                .name(f.getFileName())              // 文件名，可后续解析真实歌曲名
                .duration((int) f.getDuration())    // 强制转换为 Integer
                .contentType(f.getContentType())    // MIME 类型
                .size(f.getSize())                  // 文件大小（字节）
                .md5(f.getMd5())                    // 文件 MD5，用于去重
                .path(f.getAbsolutePath())          // 文件绝对路径
                .status(UploadStatusCode.NOT_UPLOADED.getCode())  // 上传状态,初始未上传
                .createdAt(now)                     //  时间字段 
                .updatedAt(now)                     //  时间字段
                //  MinIO 上传信息
                .bucketName(null)                   // 上传前为空
                .objectName(null)                   // 上传前为空
                .coverBucket(null)                  // 封面桶信息为空
                .coverObject(null)                  // 封面对象为空
                .build();
    }

    /**
     * 将歌曲上传事件转换为 Song 数据库实体。
     *
     * @param event 歌曲上传事件
     * @return Song 数据库实体
     */
    public static Song fromEvent(FileToSongEventDto event) {

        if (event == null) {
            throw new IllegalArgumentException("SongUploadRequestEvent 不能为空");
        }

        return Song.builder()
                // 歌曲 ID
                .id(event.getSongId())

                // 歌曲基本信息
                .name(event.getSongName()).duration(event.getDuration())

                // 文件信息
                .path(event.getFilePath()).md5(event.getMd5()).contentType(event.getContentType()).size(event.getSize())

                // MinIO 对象存储信息
                .bucketName(event.getBucketName()).objectName(event.getObjectName())

                // 封面信息
                .coverBucket(event.getCoverBucket()).coverObject(event.getCoverObject())

                .build();
    }

}
