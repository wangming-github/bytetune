package com.maizi.bytetune.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * ==============================================================
 * 类名：FolderWatcher
 * ==============================================================
 * <p>
 * 功能：
 * 使用 Java NIO WatchService 监听指定文件夹，
 * 当有新文件创建时触发回调函数。
 * <p>
 * 技术核心：
 * Java NIO2 (java.nio.file.WatchService)
 * <p>
 * 监听机制说明：
 * 1. 向操作系统注册目录监听
 * 2. 操作系统检测文件变化
 * 3. 触发事件通知 JVM
 * <p>
 * 底层实现：
 * Linux   -> inotify
 * macOS   -> kqueue
 * Windows -> ReadDirectoryChangesW
 * <p>
 * 特点：
 * - 非轮询机制（非定时扫描）
 * - 内核级事件通知
 * - 高性能
 * <p>
 * 线程模型：
 * - 单独守护线程阻塞监听
 * - 主线程不会被阻塞
 * <p>
 * 注意：
 * WatchService.take() 是阻塞方法
 * 必须在独立线程运行
 * <p>
 * ==============================================================
 */
@Slf4j
public class WatcherOnStartup {
    /* LoggerFactory */

    /**
     * 监听指定文件夹
     *
     * @param folderPath 要监听的文件夹路径
     * @param onCreate   当检测到新文件创建时执行的回调函数
     *                   <p>
     *                   使用方式示例： 扫描/music目录，有新发现执行其后的回调函数。
     *                   <p>
     *                   FolderWatcher.watchFolder("/music", path -> { System.out.println("新文件：" + path); });
     *                   <p>
     *                   执行流程：
     *                   1. 创建 WatchService
     *                   2. 注册监听事件类型
     *                   3. 启动后台线程
     *                   4. 阻塞等待事件
     *                   5. 回调处理
     */
    public static void watch(String folderPath, Consumer<Path> onCreate) {
        // 将字符串路径转换为 Path 对象
        Path path = Paths.get(folderPath);
        try {
            WatchService watchService = path.getFileSystem().newWatchService();  // 1️⃣ 创建监听器>创建 WatchService 实例（绑定当前文件系统）
            path.register(watchService, ENTRY_CREATE);   // 2️⃣ 注册目录>注册监听事件：ENTRY_CREATE=文件创建；也可监听 ENTRY_DELETE、ENTRY_MODIFY

            // 重点：
	        // •	executor.execute(() -> { ... }) 是线程池管理的线程，不允许你在内部调用 Thread.currentThread().setDaemon(true)，因为线程池线程已经由 Spring/Java 创建好了。
	        // •	IllegalThreadStateException 就是告诉你 线程状态不允许修改守护线程属性。

            Thread thread = new Thread(() -> {   // 创建独立线程执行监听逻辑（因为 watchService.take() 是阻塞方法，不能占用主线程）
                try {
                    while (true) { // 无限循环监听（线程未中断前持续运行）
                        WatchKey key = watchService.take();   // 3️⃣ 阻塞等待>  阻塞等待事件（无事件时线程进入 WAITING 状态，有事件时返回 WatchKey）
                        for (WatchEvent<?> event : key.pollEvents()) {   // 获取当前批次的所有事件
                            if (event.kind() == ENTRY_CREATE) {    // 判断是否为文件创建事件
                                Path createdPath = path.resolve((Path) event.context());    // event.context() 仅返回文件名，需要与目录拼接为完整路径
                                onCreate.accept(createdPath);    // 执行回调函数，由调用方处理业务逻辑
                            }
                        }
                        key.reset();      // 重置 WatchKey（必须调用，否则监听失效）
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();   // 恢复中断标志位，避免吞掉中断信号
                }
            }, "watcher");
            thread.setDaemon(true);    // 设置为守护线程（JVM 退出时自动结束）
            thread.start();  // 启动监听线程
        } catch (IOException e) {
            log.warn("路径不存在、无权限或文件系统不支持 WatchService");
        }
    }
}