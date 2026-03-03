package com.maizi.bytetune.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maizi.bytetune.common.constants.UploadStatus;
import com.maizi.bytetune.common.kafka.KafkaSongEventAssembler;
import com.maizi.bytetune.common.kafka.KafkaSongEventDTO;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.mapper.SongMapper;
import com.maizi.bytetune.common.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author maizi
 * @since 2026-02-26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    private final SongMapper songMapper;
    private final KafkaSongEventAssembler assembler;

    @Override
    @Transactional
    public void saveAll(List<Song> songs) {
        // // 获取代理对象再调用方法
        // @Autowired
        // private ApplicationContext context;
        // SongService proxy = context.getBean(SongService.class);
        // proxy.saveBatch(songs, 5); // 每?条单独执行 batch

        saveBatch(songs, 5); // 每?条单独执行 batch
    }

    @Override
    public List<Song> getAll() {
        return this.list();
    }

    @Override
    public boolean existsByFile(String path, String md5) {
        // 使用 LambdaQueryWrapper 避免硬编码列名
        long count = this.count(new LambdaQueryWrapper<Song>().eq(Song::getPath, path).eq(Song::getMd5, md5));
        return count > 0; // 大于 0 表示数据库已有该文件
    }

    @Override
    public List<Song> selectUnUploaded(int batchSize) {
        return lambdaQuery().eq(Song::getStatus, 0).orderByAsc(Song::getId).page(new Page<>(1, batchSize)).getRecords();
    }

    @Override
    public void updateMinioStatus(Long id, int status, String bucketName, String objectName) {
        lambdaUpdate().eq(Song::getId, id).set(Song::getStatus, status).set(Song::getBucketName, bucketName).set(Song::getObjectName, objectName).update();
    }

    /**
     * 加载待上传的歌曲，并转换为事件 DTO
     *
     * @return 需要发送到 Kafka 的事件列表
     */
    @Override
    public List<KafkaSongEventDTO> loadPendingUploadEvents() {

        // 查询状态为 未上传 或 失败 的数据
        List<Song> songs = songMapper.selectList(//
                new LambdaQueryWrapper<Song>()//
                        .in(Song::getStatus, //
                                UploadStatus.NOT_UPLOADED.getCode(),//
                                UploadStatus.FAILED.getCode()));

        if (songs.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为事件对象
        return assembler.toEvents(songs);
    }

}
