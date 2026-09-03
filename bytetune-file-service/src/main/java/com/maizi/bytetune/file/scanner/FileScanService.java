package com.maizi.bytetune.file.scanner;

import com.maizi.bytetune.contract.event.song.FileToSongEventDto;
import com.maizi.bytetune.file.config.FileServiceProperties;
import com.maizi.bytetune.file.decoder.NcmDecoder;
import com.maizi.bytetune.file.messaging.FileToSongEventDto_Producer;
import com.maizi.bytetune.file.model.SongFileInfo;
import com.maizi.bytetune.file.processor.AudioFileProcessor;
import com.maizi.bytetune.file.util.SongEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 文件扫描服务。
 *
 * <p>
 * 负责处理音乐文件扫描以及文件创建后的业务处理。
 * </p>
 *
 * <p>
 * 当前主要功能：
 * </p>
 *
 * <ul>
 *     <li>扫描已有音乐文件</li>
 *     <li>处理监听目录中新创建的文件</li>
 *     <li>识别 NCM 文件</li>
 *     <li>调用 NCM 解码器</li>
 *     <li>处理解密后的音频文件</li>
 *     <li>将歌曲信息转换为 Kafka 上传请求事件</li>
 * </ul>
 */
@Slf4j
@Service
public class FileScanService {

    /**
     * 文件服务配置。
     */
    private final FileServiceProperties properties;

    /**
     * NCM 解码器。
     */
    private final NcmDecoder ncmDecoder;

    /**
     * 文件扫描线程池。
     */
    private final Executor fileScanExecutor;

    /**
     * 歌曲上传请求事件生产者。
     */
    private final FileToSongEventDto_Producer fileToSongMsgProducer;

    /**
     * 构造文件扫描服务。
     *
     * @param properties            文件服务配置
     * @param ncmDecoder            NCM 解码器
     * @param fileScanExecutor      文件扫描线程池
     * @param fileToSongMsgProducer 歌曲上传请求事件生产者
     */
    public FileScanService(FileServiceProperties properties, NcmDecoder ncmDecoder, @Qualifier("fileScanExecutor") Executor fileScanExecutor, FileToSongEventDto_Producer fileToSongMsgProducer) {
        this.properties = properties;
        this.ncmDecoder = ncmDecoder;
        this.fileScanExecutor = fileScanExecutor;
        this.fileToSongMsgProducer = fileToSongMsgProducer;
    }

    /**
     * 扫描目录中的已有音频文件，
     * 并批量发布歌曲上传请求事件。
     *
     * <p>
     * 该方法通过文件扫描线程池异步执行，
     * 不阻塞 Spring Boot 启动流程。
     * </p>
     *
     * @param watchPath 音乐文件目录
     */
    public void scanExistingFiles(String watchPath) {

        // 将扫描任务提交到专用文件扫描线程池
        fileScanExecutor.execute(() -> {

            // 设置当前任务的 MDC 标识
            MDC.put("JOB", "[加载现有文件]");

            try {

                log.info("开始扫描已有音乐文件：{}", watchPath);

                // 扫描目录中的音乐文件
                List<SongFileInfo> files = AudioFileProcessor.scan(watchPath);

                // 没有发现音乐文件
                if (files.isEmpty()) {
                    log.info("目录中没有发现音乐文件：{}", watchPath);
                    return;
                }
                log.info("扫描完成，共发现 {} 个音乐文件", files.size());
                // 将文件信息转换为歌曲上传请求事件
                List<FileToSongEventDto> events = SongEventBuilder.toSongEventList(files);

                // 没有需要发送的事件
                if (events.isEmpty()) {
                    log.info("没有需要发送的歌曲上传事件");
                    return;
                }
                // 批量发送歌曲上传请求事件
                fileToSongMsgProducer.publishBatch(events);
                log.info("歌曲上传请求事件发送完成，数量：{}", events.size());
            } catch (Exception e) {

                log.error("扫描已有音乐文件失败：{}", watchPath, e);

            } finally {
                // 清理当前线程的 MDC 数据
                MDC.remove("JOB");
            }
        });
    }

    /**
     * 处理监听目录中新创建的文件。
     *
     * @param path 新创建的文件路径
     */
    public void handleNewFile_(Path path) {
        log.info("输出目录检测到新文件：{}", path);
        // 后续处理新文件
    }

    /**
     * 处理监听目录中新创建的文件。
     *
     * <p>
     * 根据文件扩展名决定具体处理方式：
     * </p>
     *
     * <ul>
     *     <li>.tmp：忽略</li>
     *     <li>.ncm：进行 NCM 解密</li>
     *     <li>.mp3：处理音频文件</li>
     *     <li>.flac：处理音频文件</li>
     * </ul>
     *
     * @param path 新创建的文件路径
     */
    public void handleNewFile(Path path) {

        // 不是普通文件时直接忽略
        if (!Files.isRegularFile(path)) {
            return;
        }

        log.debug("检测到新文件：{}", path);

        // 获取文件名并统一转换为小写
        String fileName = path.getFileName().toString().toLowerCase();

        // 下载临时文件，例如 xxx.flac.tmp、xxx.mp3.tmp
        if (fileName.endsWith(".tmp")) {
            log.debug("忽略下载中的临时文件：{}", path);
            return;
        }

        // NCM 文件交给 NCM 解密流程
        if (fileName.endsWith(".ncm")) {
            handleNcmFile(path);
            return;
        }

        // MP3 / FLAC 文件交给音频文件处理流程
        if (fileName.endsWith(".mp3") || fileName.endsWith(".flac")) {
            handleAudioFile(path);
            return;
        }
    }

    /**
     * 处理 NCM 文件。
     *
     * <p>
     * 调用 NcmDecoder 解密 NCM 文件，
     * 然后继续处理解密得到的音频文件。
     * </p>
     *
     * @param ncmFile NCM 文件
     */
    private void handleNcmFile(Path ncmFile) {
        log.debug("检测到 NCM 文件：{}", ncmFile);
        // 调用 NCM 解码器
        List<Path> decodedFiles = ncmDecoder.decode(ncmFile);
        // 处理解密得到的音频文件
        for (Path decodedFile : decodedFiles) {
            handleAudioFile(decodedFile);
        }
    }

    /**
     * 处理音频文件。
     *
     * <p>
     * 当前逻辑是将解密后的音频文件
     * 移动到配置的输出目录。
     * </p>
     *
     * @param audioFile 音频文件
     */
    private void handleAudioFile(Path audioFile) {
        // 将解密后的音频文件移动到输出目录
        Path target = moveToOutputDirectory(audioFile, Path.of(properties.getWatchPathOut()));
        log.info("解密后移动到输出目录【{}】完成！", target);
    }

    /**
     * 将解密后的音频文件移动到输出目录。
     *
     * @param source    原始文件
     * @param outputDir 输出目录
     * @return 移动后的文件路径
     */
    private Path moveToOutputDirectory(Path source, Path outputDir) {
        // 根据原文件名生成目标文件路径
        Path target = outputDir.resolve(source.getFileName());
        try {
            // 移动文件，如果目标文件已经存在则覆盖
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException e) {
            throw new RuntimeException("移动解密文件失败：" + source, e);
        }
    }
}