package com.maizi.bytetune.file.decoder;

import com.maizi.bytetune.file.config.FileServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcmdumpDecoder implements NcmDecoder {

    private final FileServiceProperties properties;

    @Override
    public List<Path> decode(Path ncmFile) {

        if (ncmFile == null || !Files.isRegularFile(ncmFile)) {

            throw new IllegalArgumentException("NCM 文件不存在：" + ncmFile);
        }

        String command = properties.getNcmDecoderCommand();

        if (command == null || command.isBlank()) {

            throw new IllegalStateException("未配置 ncm-decoder-command");
        }

        Path outputDirectory = Path.of(properties.getWatchPathOut());

        try {

            Files.createDirectories(outputDirectory);

            log.info("开始解密 NCM 文件：{}", ncmFile);

            Process process = new ProcessBuilder(command, ncmFile.toAbsolutePath().toString()).redirectErrorStream(true).start();

            /*
             * 必须持续消费 stdout。
             * 否则外部进程输出过多时可能因为管道阻塞而无法结束。
             */
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    log.debug("ncmdump: {}", line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {

                throw new IllegalStateException("NCM 解密失败，exitCode=" + exitCode);
            }

            List<Path> decodedFiles = findDecodedFiles(ncmFile);

            if (decodedFiles.isEmpty()) {

                throw new IllegalStateException("NCM 解密成功，但没有找到输出文件：" + ncmFile);
            }

            List<Path> outputFiles = new ArrayList<>();

            for (Path decodedFile : decodedFiles) {

                Path outputFile = moveToOutputDirectory(decodedFile, outputDirectory);

                outputFiles.add(outputFile);
            }

            log.info("NCM 解密完成：{} → {}", ncmFile, outputFiles);

            return outputFiles;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("NCM 解密进程被中断：" + ncmFile, e);

        } catch (Exception e) {

            log.error("NCM 解密失败：{}", ncmFile, e);

            throw new RuntimeException("NCM 解密失败：" + ncmFile, e);
        }
    }

    /**
     * 查找 ncmdump 生成的音频文件
     */
    private List<Path> findDecodedFiles(Path ncmFile) {

        String fileName = ncmFile.getFileName().toString();

        if (!fileName.toLowerCase().endsWith(".ncm")) {

            return List.of();
        }

        String baseName = fileName.substring(0, fileName.length() - 4);

        Path directory = ncmFile.getParent();

        List<Path> result = new ArrayList<>();

        for (String extension : List.of("mp3", "flac")) {

            Path output = directory.resolve(baseName + "." + extension);

            if (Files.isRegularFile(output)) {

                result.add(output);
            }
        }

        return result;
    }

    /**
     * 将解密后的文件移动到输出目录
     */
    private Path moveToOutputDirectory(Path source, Path outputDirectory) {

        Path target = outputDirectory.resolve(source.getFileName());

        try {

            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

            log.info("解密文件已移动：{} → {}", source, target);

            return target;

        } catch (Exception e) {

            throw new RuntimeException("移动解密文件失败：" + source, e);
        }
    }
}