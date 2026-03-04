package com.maizi.bytetune.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok 的 @RequiredArgsConstructor 会自动生成构造函数：
public class LyricServiceImpl implements LyricService {

    private final LyricRepository lyricRepository;
    private final LyricFileParser parser;

    /**
     * @param lyric
     * @return
     */
    @Override
    public Lyric save(Lyric lyric) {
        return lyricRepository.save(lyric);
    }

    @Override
    public Lyric saveFile(MultipartFile file) {
        // 自动解析歌手和歌曲名
        String originalFilename = file.getOriginalFilename(); // "拜金小姐 - 蝶恋花.lrc"
        if (originalFilename == null || !originalFilename.contains(" - ")) {
            throw new IllegalArgumentException("文件名必须包含 '歌手 - 歌曲名'");
        }
        // 去掉后缀
        String nameWithoutExt = originalFilename.replaceAll("\\.lrc$", "");
        String[] parts = nameWithoutExt.split(" - ", 2);
        String singer = parts[0].trim();
        String songName = parts[1].trim();

        // 解析文件
        Lyric lyric = null;
        try {
            lyric = parser.parseFile(file, songName, singer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lyric.setCreatedAt(LocalDateTime.now());
        return lyricRepository.save(lyric);
    }

    @Override
    public Lyric getBySong(String songName, String singer) {
        return lyricRepository.findBySongNameAndSinger(songName, singer).orElseThrow(() -> new RuntimeException("歌词不存在"));
    }

    @Override
    public void delete(String id) {
        lyricRepository.deleteById(id);
    }

    @Override
    public List<Lyric> list() {
        return lyricRepository.findAll();
    }
}