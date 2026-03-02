package com.maizi.bytetune.starter;

import com.maizi.bytetune.mongodb.LyricFileParser;
import com.maizi.bytetune.mongodb.Lyric;
import com.maizi.bytetune.mongodb.LyricLine;
import com.maizi.bytetune.mongodb.LyricRepository;
import com.maizi.bytetune.mongodb.LyricServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class) // 启用 Mockito 注解
class LyricServiceImplTest {

    @Mock
    private LyricRepository lyricRepository;

    @Mock
    private LyricFileParser parser;

    @InjectMocks
    private LyricServiceImpl lyricService;

    @Test
    void testSave() {
        Lyric lyric = new Lyric();
        lyric.setSongName("蝶恋花");
        lyric.setSinger("拜金小姐");

        Mockito.when(lyricRepository.save(lyric)).thenReturn(lyric);

        Lyric result = lyricService.save(lyric);

        assertNotNull(result);
        assertEquals("蝶恋花", result.getSongName());
        Mockito.verify(lyricRepository).save(lyric);
    }

    @Test
    void testSave1() {
        Lyric lyric = new Lyric();
        lyric.setSongName("蝶恋花");
        lyric.setSinger("拜金小姐");

        // 模拟 Repository 行为
        Mockito.when(lyricRepository.save(lyric)).thenReturn(lyric);
        Lyric result = lyricService.save(lyric);
        assertNotNull(result);
        Mockito.verify(lyricRepository).save(lyric); // 验证 save 方法被调用
    }

    @Test
    void testGetBySong() {
        Lyric lyric = new Lyric();
        lyric.setSongName("蝶恋花");
        lyric.setSinger("拜金小姐");

        Mockito.when(lyricRepository.findAll()).thenReturn(Collections.singletonList(lyric));
        Lyric result = lyricService.list().stream().filter(l -> l.getSongName().equals("蝶恋花") && l.getSinger().equals("拜金小姐")).findFirst().orElse(null);
        assertNotNull(result);
    }

    @Test
    void testList() {
        Lyric l1 = new Lyric();
        Lyric l2 = new Lyric();

        List<Lyric> list = Arrays.asList(l1, l2);

        Mockito.when(lyricRepository.findAll()).thenReturn(list);

        List<Lyric> result = lyricService.list();

        assertEquals(2, result.size());
        Mockito.verify(lyricRepository).findAll();
    }

    @Test
    void testSaveFile() throws Exception {
        // 模拟上传的 .lrc 文件
        String content = "[00:01.00]测试歌词行1\n[00:02.00]测试歌词行2";
        MockMultipartFile file = new MockMultipartFile("file", "拜金小姐 - 蝶恋花.lrc", "text/plain", content.getBytes());

        // 模拟解析器返回
        List<LyricLine> lines = Arrays.asList(new LyricLine(1000L, "测试歌词行1"), new LyricLine(2000L, "测试歌词行2"));
        Lyric parsedLyric = new Lyric();
        parsedLyric.setSongName("蝶恋花");
        parsedLyric.setSinger("拜金小姐");
        parsedLyric.setLines(lines);

        Mockito.when(parser.parseFile(file, "蝶恋花", "拜金小姐")).thenReturn(parsedLyric);
        Mockito.when(lyricRepository.save(any(Lyric.class))).thenReturn(parsedLyric);

        Lyric result = lyricService.saveFile(file);

        assertNotNull(result);
        assertEquals(2, result.getLines().size());
        Mockito.verify(parser).parseFile(file, "蝶恋花", "拜金小姐");
        Mockito.verify(lyricRepository).save(parsedLyric);
    }

}