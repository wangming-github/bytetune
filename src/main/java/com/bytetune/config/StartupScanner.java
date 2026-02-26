package com.bytetune.config;

import com.bytetune.entity.Song;
import com.bytetune.service.ISongService;
import com.bytetune.util.FolderWatcher;
import com.bytetune.dto.SongFileInfo;
import com.bytetune.util.SongFileScanner;
import com.bytetune.util.SongFileScannerExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 应用启动时自动扫描本地音乐并入库
 */
@Configuration
public class StartupScanner {

    private static final Logger log = LoggerFactory.getLogger(StartupScanner.class);

    private final ISongService songService;

    public StartupScanner(ISongService songService) {
        this.songService = songService;
    }

    private final String musicFolder = "/Users/zimai/Music/云音乐转换mp3";
    // 批量缓存，用于批量入库
    private final List<Song> batchCache = new ArrayList<>();
    private static final int BATCH_SIZE = 5; // 每批次入库数量

    /**
     * 执行流程：
     * 1.	Spring Boot 启动 → 初始化所有 Bean
     * 2.	上下文完成 → 调用所有 CommandLineRunner.run(args)
     * 3.	你的初始化逻辑执行
     *
     * @param songService
     * @return
     */
    @Bean
    public CommandLineRunner scanLocalMusic(ISongService songService) {
        return args -> {

            String musicFolder = "/Users/zimai/Music/云音乐转换mp3"; // 本地音乐目录
            List<SongFileInfo> files = SongFileScanner.scanFolder(musicFolder);   // 扫描本地音乐文件
            List<com.bytetune.entity.Song> songs = SongFileScannerExt.toEntityList(files);   // 转换为数据库实体

            // 过滤已经存在的歌曲
            List<com.bytetune.entity.Song> newSongs = songs.stream().filter(song -> !songService.existsByFileUrl(song.getFileUrl())).toList();

            // // 判断是否有新增歌曲需要入库 newSongs 是已经经过去重过滤的列表
            // if (!newSongs.isEmpty()) {
            //     songService.saveAll(newSongs);// 数据库批量保存新增歌曲
            //     log.info("扫描并入库完成，新增歌曲数量：{}", newSongs.size());
            // } else {
            //     log.info("没有新的数据需要导入！");
            // }

            Optional.of(newSongs).filter(list -> !list.isEmpty()).ifPresentOrElse(list -> {
                songService.saveAll(list);
                log.info("扫描并入库完成，新增歌曲数量：{}", list.size());
            }, () -> log.debug("没有新的数据需要导入！"));

            // 启动 FolderWatcher 监听指定文件夹，当有新文件创建时，调用当前类的 handleNewFile 方法处理新文件
            FolderWatcher.watchFolder(musicFolder, this::handleNewFile);
        };
    }

    /**
     * 处理新文件
     * 1. 判断是否为音频文件
     * 2. 解析信息
     * 3. 去重
     * 4. 批量入库
     *
     * @param path 新文件路径
     */
    public void handleNewFile(Path path) {
        try {
            if (!isAudioFile(path)) return;
            Song song = parseSong(path);

            // 去重，文件已存在数据库则跳过
            if (songService.existsByFileUrl(song.getFileUrl())) {
                log.debug("文件已存在，跳过入库：{}", song.getFileUrl());
                return;
            }
            // 加入批量缓存
            batchCache.add(song);
            if (batchCache.size() >= BATCH_SIZE) {
                flushBatch();
            }
        } catch (Exception e) {
            log.error("处理新文件失败: {}", path.toAbsolutePath(), e);
        }
    }

    /**
     * 判断文件是否为音频文件
     */
    private boolean isAudioFile(Path path) {
        try {
            String mimeType = SongFileScanner.detectMimeType(path.toFile());
            return mimeType.startsWith("audio");
        } catch (Exception e) {
            log.error("检测文件类型失败: {}", path.toAbsolutePath(), e);
            return false;
        }
    }

    /**
     * 解析 Path 为 Song 实体
     */
    private Song parseSong(Path path) throws Exception {
        return SongFileScannerExt.toEntity(SongFileScanner.getSongFileInfo(path.toFile()));
    }

    /**
     * 批量入库缓存中的歌曲
     */
    public void flushBatch() {
        if (batchCache.isEmpty()) return;
        songService.saveAll(new ArrayList<>(batchCache));
        log.info("批量入库完成，数量：{}", batchCache.size());
        batchCache.clear();
    }

}