package com.bytetune.config;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.bytetune.dto.SongFileInfo;
import com.bytetune.entity.Song;
import com.bytetune.exception.BatchDuplicateException;
import com.bytetune.service.ISongService;
import com.bytetune.util.FolderWatcher;
import com.bytetune.util.SongFileScanner;
import com.bytetune.util.SongFileScannerExt;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用启动时自动扫描本地音乐并入库
 */
@Slf4j
@Configuration
public class StartupScanner {

    private final ISongService songService;

    public StartupScanner(ISongService songService) {
        this.songService = songService;
    }

    private final String musicFolder = "/Users/zimai/Music/云音乐转换mp3";
    // 批量缓存，用于批量入库
    private final List<Song> buffer = new ArrayList<>();
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
            final int BATCH_SIZE = 20;

            List<SongFileInfo> files = SongFileScanner.scanFolder(musicFolder);
            processFilesBatch(songService, files, BATCH_SIZE);

            // 启动 FolderWatcher 监听指定文件夹，当有新文件创建时，调用当前类的 handleNewFile 方法处理新文件
            FolderWatcher.watchFolder(musicFolder, this::handleNewFile);
        };
    }

    /**
     * 第一次启动项目时扫描文件列表并批量入库数据库
     *
     * @param songService ISongService 实例
     * @param files       待处理的本地文件信息列表
     * @param batchSize   批量插入大小
     */
    public void processFilesBatch(ISongService songService, List<SongFileInfo> files, int batchSize) {
        if (files == null || files.isEmpty()) {
            log.debug("没有需要处理的文件！");
            return;
        }

        // 转换为数据库实体
        List<Song> songs = files.stream().map(SongFileScannerExt::toEntity).toList();

        // 过滤已经存在的歌曲（path + md5）
        List<Song> newSongs = songs.stream().filter(song -> !songService.existsByFile(song.getPath(), song.getMd5())).toList();

        if (newSongs.isEmpty()) {
            log.info("没有新的数据需要导入！");
            return;
        }

        // 批量保存到数据库
        for (int i = 0; i < newSongs.size(); i += batchSize) {
            List<Song> batch = newSongs.subList(i, Math.min(i + batchSize, newSongs.size()));
            try {
                songService.saveBatch(songs);
            } catch (BatchDuplicateException e) {
                // 可以在这里做特殊处理，比如记录、跳过、回调等
                log.info("批量保存遇到重复数据: {}", e.getMessage());
            } catch (Exception e) {
                throw e;
            }

            log.info("扫描并入库完成，新增歌曲数量：{}", batch.size());
        }
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
            if (songService.existsByFile(song.getPath(), song.getMd5())) {
                log.debug("文件已存在，跳过入库：{}", song.getObjectName());
                return;
            }
            // 加入批量缓存
            buffer.add(song);
            if (buffer.size() >= BATCH_SIZE) {
                flushBuffer();
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
    public void flushBuffer() {
        if (buffer.isEmpty()) return;
        songService.saveAll(new ArrayList<>(buffer));
        log.debug("批量入库完成，数量：{}", buffer.size());
        buffer.clear();
    }

    /**
     * 每 ? 秒强制提交
     * OR
     * LinkedBlockingQueue + 单线程消费线程
     */
    @Scheduled(fixedDelay = 3000)
    public void autoFlush() {
        synchronized (this) {
            if (!buffer.isEmpty()) {
                log.debug("3秒定时提交触发，当前缓存数量：{}", buffer.size());
                flushBuffer();
            }
        }
    }

}