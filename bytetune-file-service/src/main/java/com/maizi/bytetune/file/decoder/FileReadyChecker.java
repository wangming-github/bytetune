package com.maizi.bytetune.file.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class FileReadyChecker {

    /**
     * 判断文件是否已经停止写入
     *
     * @param path       文件
     * @param checkCount 检查次数
     * @param interval   检查间隔，单位毫秒
     */
    public boolean waitUntilReady(Path path, int checkCount, long interval) {

        long previousSize = -1;

        for (int i = 0; i < checkCount; i++) {

            if (!Files.isRegularFile(path)) {
                return false;
            }

            try {

                long currentSize = Files.size(path);

                if (currentSize > 0 && currentSize == previousSize) {

                    return true;
                }

                previousSize = currentSize;

                Thread.sleep(interval);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                return false;

            } catch (Exception e) {

                log.warn("检查文件状态失败：{}", path, e);

                return false;
            }
        }

        return false;
    }
}