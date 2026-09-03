package com.maizi.bytetune.file.watcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * 文件夹监听器。
 *
 * <p>
 * 使用 Java NIO WatchService 监听指定目录，
 * 当目录中创建新文件时，通过回调通知业务层。
 * </p>
 *
 * <p>
 * 该类只负责：
 * </p>
 *
 * <ul>
 *     <li>创建 WatchService</li>
 *     <li>注册目录监听</li>
 *     <li>等待文件系统事件</li>
 *     <li>将新文件路径回调给调用方</li>
 * </ul>
 *
 * <p>
 * 不负责具体文件业务处理，例如：
 * NCM 解密、音频解析、文件上传、Kafka 消息发送等。
 * </p>
 *
 * <p>
 * WatchService.take() 是阻塞方法，
 * 因此调用 watch() 时应该放在独立线程中执行。
 * </p>
 */
@Slf4j
@Component
public class FolderWatcher {

    /**
     * 监听指定目录中的文件创建事件。
     *
     * @param directory 要监听的目录
     * @param handler   文件创建事件处理器
     */
    public void watch(Path directory, FileCreatedHandler handler) {

        try {

            // 创建 WatchService，用于接收操作系统发送的文件系统事件
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // 注册目录，并只监听文件创建事件
            directory.register(watchService, ENTRY_CREATE);

            log.debug("开始监听目录：{}", directory);

            // 持续监听，直到当前线程收到中断信号
            while (!Thread.currentThread().isInterrupted()) {

                // 阻塞等待文件系统事件
                WatchKey key = watchService.take();

                // 获取本次产生的所有文件系统事件
                for (WatchEvent<?> event : key.pollEvents()) {

                    // 当前类只处理文件创建事件
                    if (event.kind() != ENTRY_CREATE) {
                        continue;
                    }

                    // event.context() 返回的是相对于监听目录的路径
                    Path relativePath = (Path) event.context();

                    // 将相对路径转换为完整文件路径
                    Path fullPath = directory.resolve(relativePath);

                    log.debug("检测到新文件：{}", fullPath);

                    // 将文件路径交给业务层处理
                    handler.handle(fullPath);
                }

                // 重置 WatchKey，使其继续接收后续文件系统事件
                if (!key.reset()) {

                    log.warn("目录监听失效：{}", directory);

                    break;
                }
            }

        } catch (InterruptedException e) {

            // 恢复线程中断状态，避免吞掉中断信号
            Thread.currentThread().interrupt();

            log.info("目录监听线程结束：{}", directory);

        } catch (IOException e) {

            // 创建 WatchService 或注册目录失败
            throw new RuntimeException("启动目录监听失败：" + directory, e);
        }
    }

    /**
     * 文件创建事件处理器。
     *
     * <p>
     * FolderWatcher 只负责发现文件，
     * 具体如何处理文件由调用方决定。
     * </p>
     */
    @FunctionalInterface
    public interface FileCreatedHandler {

        /**
         * 处理新创建的文件。
         *
         * @param path 新创建的文件路径
         */
        void handle(Path path);
    }
}