package com.maizi.bytetune.file.watcher;

import com.maizi.bytetune.file.config.FileServiceProperties;
import com.maizi.bytetune.file.scanner.FileScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * 文件监听启动配置。
 *
 * <p>
 * 负责在 Spring Boot 应用启动完成后，
 * 启动文件夹监听任务。
 * </p>
 *
 * <p>
 * 当前类主要包含两部分：
 * </p>
 *
 * <ul>
 *     <li>旧的静态 watch() 方法，用于直接创建独立守护线程监听目录</li>
 *     <li>新的 Spring Boot 启动方式，通过 FolderWatcher + FileScanService 完成监听</li>
 * </ul>
 *
 * <p>
 * WatchService.take() 是阻塞操作，
 * 因此监听任务不能直接阻塞 Spring Boot 主启动线程。
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WatcherOnStartup {

    private final FileServiceProperties properties;

    private final FolderWatcher folderWatcher;

    private final FileScanService fileScanService;

    /**
     * 监听指定文件夹。
     *
     * <p>
     * 该方法通过 Java NIO WatchService 监听目录，
     * 当检测到 ENTRY_CREATE 文件创建事件时，
     * 调用传入的回调函数处理新文件。
     * </p>
     *
     * <p>
     * 执行流程：
     * </p>
     *
     * <ol>
     *     <li>将字符串路径转换为 Path</li>
     *     <li>创建 WatchService</li>
     *     <li>注册目录监听</li>
     *     <li>创建独立守护线程</li>
     *     <li>阻塞等待文件创建事件</li>
     *     <li>获取新文件完整路径</li>
     *     <li>调用 onCreate 回调</li>
     * </ol>
     *
     * @param folderPath 要监听的文件夹路径
     * @param onCreate   文件创建后的回调函数
     */
    public static void watch(String folderPath, Consumer<Path> onCreate) {

        // 将字符串路径转换为 Path
        Path path = Paths.get(folderPath);

        try {

            // 创建 WatchService
            WatchService watchService = path.getFileSystem().newWatchService();

            // 注册目录，只监听文件创建事件
            path.register(watchService, ENTRY_CREATE);

            /*
             * WatchService.take() 是阻塞方法，
             * 因此使用独立线程执行监听逻辑，
             * 避免阻塞调用方线程。
             */
            Thread thread = new Thread(() -> {

                try {

                    // 持续监听文件创建事件
                    while (true) {

                        // 阻塞等待文件系统事件
                        var key = watchService.take();

                        // 获取本次产生的所有事件
                        for (var event : key.pollEvents()) {

                            // 只处理文件创建事件
                            if (event.kind() == ENTRY_CREATE) {

                                // 获取新创建文件的完整路径
                                Path createdPath = path.resolve((Path) event.context());

                                // 将文件路径交给调用方处理
                                onCreate.accept(createdPath);
                            }
                        }

                        // 重置 WatchKey，
                        // 使其继续监听后续事件
                        key.reset();
                    }

                } catch (InterruptedException e) {

                    // 恢复中断状态
                    Thread.currentThread().interrupt();
                }
            }, "watcher");

            // 设置为守护线程，JVM 退出时自动结束
            thread.setDaemon(true);

            // 启动监听线程
            thread.start();

        } catch (IOException e) {

            log.warn("路径不存在、无权限或文件系统不支持 WatchService");
        }
    }

    /**
     * Spring Boot 启动完成后启动文件监听。
     *
     * <p>
     * 监听目录由：
     * {@code bytetune-file-service.watch-path-in}
     * 配置。
     * </p>
     *
     * @return CommandLineRunner 启动任务
     */
    @Bean
    public CommandLineRunner startFileWatcher_new() {

        return args -> {

            // 获取配置中的输入目录
            Path watchPath = Path.of(properties.getWatchPathIn());

            log.info("启动文件服务，监听【下载】目录：{}", watchPath);

            /*
             * 使用独立线程执行 FolderWatcher，
             * 因为 WatchService.take() 会一直阻塞等待事件。
             */
            Thread watcherThread = new Thread(() -> folderWatcher.watch(watchPath, fileScanService::handleNewFile), "file-watcher");

            // 设置为守护线程
            watcherThread.setDaemon(true);

            // 启动文件监听线程
            watcherThread.start();
        };
    }
}