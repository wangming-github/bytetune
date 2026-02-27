package com.bytetune.util;

import com.bytetune.dto.SongFileInfo;
import com.bytetune.entity.Song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SongFileScanner 扩展方法：把扫描到的文件信息转换成 Song 实体
 */

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
    public static List<Song> toEntityList(List<SongFileInfo> files) {
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
    public static Song toEntity(SongFileInfo f) {
        LocalDateTime now = LocalDateTime.now();
        //  基础文件信息
        return Song.builder()                       //
                .name(f.getFileName())              // 文件名，可后续解析真实歌曲名
                .duration((int) f.getDuration())    // 强制转换为 Integer
                .contentType(f.getContentType())    // MIME 类型
                .size(f.getSize())                  // 文件大小（字节）
                .md5(f.getMd5())                    // 文件 MD5，用于去重
                .path(f.getAbsolutePath())          // 文件绝对路径
                .status(UploadStatus.NOT_UPLOADED.getCode())  // 上传状态 
                .createdAt(now)                     //  时间字段 
                .updatedAt(now)                     //  时间字段
                //  MinIO 上传信息
                .bucketName(null)                   // 上传前为空
                .objectName(null)                   // 上传前为空
                .coverBucket(null)                  // 封面桶信息为空
                .coverObject(null)                  // 封面对象为空
                .build();
    }
}