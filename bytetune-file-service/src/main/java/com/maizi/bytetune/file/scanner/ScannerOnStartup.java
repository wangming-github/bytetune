package com.maizi.bytetune.file.scanner;

import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.file.config.FileServiceProperties;
import com.maizi.bytetune.file.messaging.FileToSongEventDto_Producer;
import com.maizi.bytetune.file.model.SongFileInfo;
import com.maizi.bytetune.file.processor.AudioFileProcessor;
import com.maizi.bytetune.file.util.SongEventBuilder;
import com.maizi.bytetune.file.watcher.WatcherOnStartup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.nio.file.Path;
import java.util.List;

/**
 * 应用启动时自动扫描本地音乐。
 *
 * <p>
 * 当前类负责在 Spring Boot 启动后：
 * </p>
 *
 * <ul>
 *     <li>扫描已有音乐文件</li>
 *     <li>将文件信息转换为歌曲上传请求事件</li>
 *     <li>批量发送歌曲上传请求事件</li>
 *     <li>启动文件夹监听</li>
 * </ul>
 *
 * <p>
 * 注意：
 * 当前类中存在两套启动逻辑，
 * 后续整理时可以统一到 FileScanService 和 WatcherOnStartup。
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScannerOnStartup {

    /*
     * 后续通过 Kafka 消息发送给 bytetune-song-service，
     * 因此这里不再直接依赖 SongService。
     *
     * TODO：
     * 由 bytetune-song-service 负责歌曲后续业务处理。
     */
    // private final SongService songService;
    // private final SongExtService songExtService;

    /**
     * 文件服务配置。
     */
    private final FileServiceProperties fileServiceProperties;

    /**
     * 异步任务执行器。
     */
    private final TaskExecutor executor;

    /**
     * 歌曲上传请求事件生产者。
     */
    private final FileToSongEventDto_Producer fileToSongMsgProducer;

    /**
     * 旧版文件扫描服务。
     */
    private final FileScanService fileScanService;

    /**
     * 每批次处理的文件数量。
     */
    private static final int BATCH_SIZE = 5;

    /**
     * 应用启动后执行旧版扫描初始化逻辑。
     *
     * <p>
     * 执行流程：
     * </p>
     *
     * <ol>
     *     <li>Spring Boot 初始化 Bean</li>
     *     <li>ApplicationContext 完成初始化</li>
     *     <li>执行 CommandLineRunner</li>
     *     <li>提交异步扫描任务</li>
     *     <li>扫描已有音乐文件</li>
     *     <li>转换为 SongUploadRequestEvent</li>
     *     <li>批量发送 Kafka 消息</li>
     *     <li>启动目录监听</li>
     * </ol>
     *
     * <p>
     * 当前方法属于旧版实现，保留原有逻辑。
     * </p>
     */
    @Bean
    public CommandLineRunner init_Bak() {

        // 获取需要扫描的目录
        String watchPath = fileServiceProperties.getWatchPathOut();
        log.info("⏳扫描目录：{}", watchPath);
        return args -> {

            // 将扫描任务提交到异步执行器
            executor.execute(() -> {
                // 给当前线程设置扫描任务名称
                Thread.currentThread().setName("init-scan");
                try {
                    log.info("⏳加载现有文件到数据库,请稍后...⏳");
                    // 扫描目录中的音乐文件
                    List<SongFileInfo> files = AudioFileProcessor.scan(watchPath);

                    // 将文件信息转换为上传请求事件
                    List<FileToSongEventDto> songEventList = SongEventBuilder.toSongEventList(files);

                    // 批量发送上传请求事件
                    fileToSongMsgProducer.publishBatch(songEventList);

                } catch (Exception e) {
                    log.error("扫描文件夹失败", e);
                }
            });

            /*
             * 启动 FolderWatcher 监听指定目录，
             * 当发现新文件时调用当前类的 handleNewFile 方法。
             */
            WatcherOnStartup.watch(watchPath, this::handleNewFile);
        };
    }

    /**
     * 应用启动后执行新的文件扫描初始化逻辑。
     *
     * <p>
     * 当前逻辑：
     * </p>
     *
     * <ol>
     *     <li>获取输出目录</li>
     *     <li>异步扫描已有文件</li>
     *     <li>启动文件监听</li>
     * </ol>
     *
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner init() {

        return args -> {

            // 获取需要监听的输出目录
            String watchPath = fileServiceProperties.getWatchPathOut();

            log.info("启动文件服务，监听【输出】目录：{}", watchPath);

            // 异步扫描已有文件
            fileScanService.scanExistingFiles(watchPath);

            // 启动文件监听
            WatcherOnStartup.watch(watchPath, fileScanService::handleNewFile);
        };
    }

    /**
     * 处理监听到的新文件。
     *
     * <p>
     * 当前处理流程：
     * </p>
     *
     * <ol>
     *     <li>判断是否为音频文件</li>
     *     <li>解析音频文件信息</li>
     *     <li>构建 SongUploadRequestEvent</li>
     *     <li>发送 Kafka 消息</li>
     * </ol>
     *
     * @param path 新文件路径
     */
    public void handleNewFile(Path path) {

        log.info("扫描到新的文件{}", path);

        try {
            // 判断是否为音频文件
            if (!isAudioFile(path)) {
                return;
            }
            // 将文件解析为歌曲上传事件
            FileToSongEventDto song = parseSongUploadedEvent(path);
            /*
             * TODO：
             * 发送消息，由 song-service 负责后续业务处理。
             */
            fileToSongMsgProducer.publish(song);
        } catch (Exception e) {
            log.error("处理新文件失败: {}", path.toAbsolutePath(), e);
        }
    }

    /**
     * 判断文件是否为音频文件。
     *
     * @param path 文件路径
     * @return true 表示音频文件
     */
    private boolean isAudioFile(Path path) {

        try {

            // 根据文件内容检测 MIME 类型
            String mimeType = AudioFileProcessor.detectMimeType(path.toFile());

            return mimeType.startsWith("audio");

        } catch (Exception e) {

            log.error("检测文件类型失败: {}", path.toAbsolutePath(), e);

            return false;
        }
    }

    /**
     * 将 Path 解析为 SongUploadRequestEvent。
     *
     * @param path 音频文件路径
     * @return 歌曲上传请求事件
     */
    private FileToSongEventDto parseSongUploadedEvent(Path path) throws Exception {
        return SongEventBuilder.toEvent(AudioFileProcessor.getSongFileInfo(path.toFile()));
    }
}