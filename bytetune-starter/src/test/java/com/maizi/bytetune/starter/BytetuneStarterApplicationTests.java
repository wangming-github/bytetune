package com.maizi.bytetune.starter;

import com.maizi.bytetune.mongodb.Lyric;
import com.maizi.bytetune.mongodb.LyricLine;
import com.maizi.bytetune.mongodb.LyricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class BytetuneStarterApplicationTests {

    @Autowired
    LyricService lyricService;

    private List<Lyric> generateTestLyrics() {
        LocalDateTime now = LocalDateTime.now();

        return List.of(new Lyric(null, "晴天", "周杰伦", "专辑1", "zh", "网易", List.of(new LyricLine(1000L, "故事的小黄花"), new LyricLine(5000L, "从出生那年就飘着")), 2, 180L, now, now),

                new Lyric(null, "夜曲", "周杰伦", "专辑2", "zh", "网易", List.of(new LyricLine(1000L, "黑色的夜"), new LyricLine(4000L, "你走的街头")), 2, 200L, now, now),

                new Lyric(null, "稻香", "周杰伦", "专辑3", "zh", "网易", List.of(new LyricLine(1000L, "对这个世界如果你有太多抱怨"), new LyricLine(5000L, "跌倒了就不怕")), 2, 210L, now, now),

                new Lyric(null, "告白气球", "周杰伦", "专辑4", "zh", "网易", List.of(new LyricLine(1000L, "塞纳河畔 左岸的咖啡"), new LyricLine(3000L, "我手一杯")), 2, 230L, now, now),

                new Lyric(null, "七里香", "周杰伦", "专辑5", "zh", "网易", List.of(new LyricLine(1000L, "窗外的麻雀 在电线杆上多嘴"), new LyricLine(4000L, "你说这一句 很有夏天的感觉")), 2, 240L, now, now),

                new Lyric(null, "青花瓷", "周杰伦", "专辑6", "zh", "网易", List.of(new LyricLine(1000L, "素胚勾勒出青花笔锋浓转淡"), new LyricLine(4000L, "瓶身描绘的牡丹一如你初妆")), 2, 250L, now, now),

                new Lyric(null, "不能说的秘密", "周杰伦", "专辑7", "zh", "网易", List.of(new LyricLine(1000L, "你的微笑像天使一样"), new LyricLine(3000L, "每一次出现 都让我心动")), 2, 220L, now, now),

                new Lyric(null, "听妈妈的话", "周杰伦", "专辑8", "zh", "网易", List.of(new LyricLine(1000L, "天青色等烟雨"), new LyricLine(4000L, "而我在等你")), 2, 205L, now, now),

                new Lyric(null, "菊花台", "周杰伦", "专辑9", "zh", "网易", List.of(new LyricLine(1000L, "风再起时"), new LyricLine(3000L, "残酷的记忆")), 2, 215L, now, now),

                new Lyric(null, "珊瑚海", "周杰伦", "专辑10", "zh", "网易", List.of(new LyricLine(1000L, "你握着我的手"), new LyricLine(3000L, "却带我到海边")), 2, 225L, now, now));
    }

    @Test
    void saveMongo() {
        List<Lyric> lyrics = generateTestLyrics();
        List<Lyric> saved = lyrics.stream().map(lyricService::save).toList();
        System.out.println("已保存 " + saved.size() + " 条");
    }

}
