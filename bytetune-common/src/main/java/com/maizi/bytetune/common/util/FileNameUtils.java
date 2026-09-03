package com.maizi.bytetune.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件名工具类
 */
public final class FileNameUtils {

    private FileNameUtils() {
    }

    /**
     * 从文件路径中获取不带扩展名的文件名。
     *
     * @param path 文件路径
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(Path path) {
        if (path == null) {
            return null;
        }

        return getFileNameWithoutExtension(path.getFileName().toString());
    }

    /**
     * 从文件路径中获取不带扩展名的文件名。
     *
     * @param path 文件路径
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        String fileName = Paths.get(path).getFileName().toString();

        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex <= 0) {
            return fileName;
        }

        return fileName.substring(0, extensionIndex);
    }

    /**
     * 从文件路径中获取完整文件名。
     * 包含文件扩展名，不包含父目录路径。
     *
     * @param path 文件路径
     * @return 完整文件名
     */
    public static String getFileName(Path path) {
        if (path == null) {
            return null;
        }
        Path fileName = path.getFileName();
        return fileName == null ? null : fileName.toString();
    }

    /**
     * 从文件路径中获取完整文件名。
     * <p>   包含文件扩展名，不包含父目录路径。
     *
     * @param path 文件路径
     * @return 完整文件名
     */
    public static String getFileName(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return Paths.get(path).getFileName().toString();
    }
}