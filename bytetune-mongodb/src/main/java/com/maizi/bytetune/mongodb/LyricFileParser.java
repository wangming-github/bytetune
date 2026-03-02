package com.maizi.bytetune.mongodb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class LyricFileParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 解析上传的 LRC/JSON 行混合文件
     * 返回 Lyric 对象
     */
    public Lyric parseFile(MultipartFile file, String songName, String singer) throws Exception {
        List<LyricLine> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // JSON 行
                if (line.startsWith("{")) {
                    JsonNode node = mapper.readTree(line);
                    long time = node.get("t").asLong();

                    StringBuilder text = new StringBuilder();
                    for (JsonNode c : node.get("c")) {
                        text.append(c.get("tx").asText());
                    }

                    lines.add(new LyricLine(time, text.toString()));
                }
                // LRC 行
                else if (line.startsWith("[")) {
                    int right = line.indexOf("]");
                    if (right == -1) continue;

                    String timeStr = line.substring(1, right);
                    String text = line.substring(right + 1);

                    long time = parseLrcTime(timeStr);
                    lines.add(new LyricLine(time, text));
                }
            }
        }

        // 按时间排序
        lines.sort(Comparator.comparing(LyricLine::getTime));

        int lineCount = lines.size();
        long duration = lineCount == 0 ? 0 : lines.get(lineCount - 1).getTime();

        return new Lyric(null, songName, singer, null, "zh", "upload", lines, lineCount, duration, LocalDateTime.now(), LocalDateTime.now());
    }

    // LRC 格式时间转换 [mm:ss.SS] -> 毫秒
    private long parseLrcTime(String timeStr) {
        String[] minSec = timeStr.split(":");
        String[] secMilli = minSec[1].split("\\.");
        long min = Long.parseLong(minSec[0]);
        long sec = Long.parseLong(secMilli[0]);
        long milli = secMilli.length > 1 ? Long.parseLong(secMilli[1]) * 10 : 0;
        return min * 60_000 + sec * 1000 + milli;
    }
}