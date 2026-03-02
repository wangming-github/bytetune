package com.maizi.bytetune.file;

import com.maizi.bytetune.common.dto.SongFileInfo;
import com.maizi.bytetune.common.entity.Song;
import com.maizi.bytetune.common.service.SongExtService;
import com.maizi.bytetune.common.service.SongService;
import com.maizi.bytetune.common.util.SongEntityBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用启动时自动扫描本地音乐并入库
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class StartupScanner {

    private final SongService songService;
    private final SongExtService songExtService;
    private final FileProperties fileProperties;
    private final TaskExecutor executor;

    // 批量缓存，用于批量入库
    private final List<Song> buffer = new ArrayList<>();
    private static final int BATCH_SIZE = 5; // 每批次入库数量

    /**
     * 执行流程：
     * 1.	Spring Boot 启动 → 初始化所有 Bean
     * 2.	上下文完成 → 调用所有 CommandLineRunner.run(args)
     * 3.	你的初始化逻辑执行
     * <p>
     * Lambda/线程池里的 Runnable 不允许受检异常，必须处理。
     * •	解决方法就是 try/catch 或者包装成 unchecked。
     * •	这就是为什么放在 executor 里会编译报错，而直接放在 CommandLineRunner.run 里不会。
     * <p>
     */
    @Bean
    public CommandLineRunner initScanner() {
        return args -> {
            executor.execute(() -> {
                Thread.currentThread().setName("init-scan"); // 给当前线程临时改名
                try {
                    // 异步执行扫描和入库逻辑
                    MDC.put("JOB", "[加载现有文件到数据库]");
                    log.info("加载现有文件到数据库,请稍后...");
                    // 第一次启动项目时扫描文件列表并批量入库数据库
                    List<SongFileInfo> files = AudioFileProcessor.scan(fileProperties.getWatchPath());
                    songExtService.loadExistingSongs(files);
                } catch (Exception e) {
                    log.error("扫描文件夹失败", e);
                } finally {
                    MDC.clear();
                }
            });
            // 启动 FolderWatcher 监听指定文件夹，当有新文件创建时，调用当前类的 handleNewFile 方法处理新文件
            WatcherFolder.watch(fileProperties.getWatchPath(), this::handleNewFile);
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
        MDC.put("JOB", "[监听指定文件夹]");
        log.info("扫描到新的文件{}", path);
        MDC.put("songId", "[" + path + "]");
        try {
            if (!isAudioFile(path)) return;
            log.debug("是音频文件");
            Song song = parseSong(path);

            // 去重，文件已存在数据库则跳过
            if (songService.existsByFile(song.getPath(), song.getMd5())) {
                log.info("文件已入库");
                return;
            }
            log.info("未入库，加入批量缓存等待处理。");
            // 加入批量缓存
            buffer.add(song);
            if (buffer.size() >= BATCH_SIZE) {
                flushBuffer();
            }
        } catch (Exception e) {
            log.error("处理新文件失败: {}", path.toAbsolutePath(), e);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 判断文件是否为音频文件
     */
    private boolean isAudioFile(Path path) {
        try {
            String mimeType = AudioFileProcessor.detectMimeType(path.toFile());
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
        return SongEntityBuilder.toEntity(AudioFileProcessor.getSongFileInfo(path.toFile()));
    }

    /**
     * 批量入库缓存中的歌曲
     */
    public void flushBuffer() {
        if (buffer.isEmpty()) return;
        songService.saveAll(new ArrayList<>(buffer));
        log.debug("批量缓存入库完成，数量：{}", buffer.size());
        buffer.clear();
    }

    /**
     * 每 ? 秒强制提交
     * OR
     * LinkedBlockingQueue + 单线程消费线程
     */
    @Scheduled(fixedDelay = 10_000)
    public void autoFlush() {
        synchronized (this) {
            if (!buffer.isEmpty()) {
                log.debug("3秒定时提交触发，当前缓存数量：{}", buffer.size());
                flushBuffer();
            }
        }
    }

}