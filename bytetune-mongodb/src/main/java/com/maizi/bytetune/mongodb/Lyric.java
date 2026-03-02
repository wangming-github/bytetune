package com.maizi.bytetune.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document("lyrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Lyric", description = "歌曲歌词文档（MongoDB 存储）")
public class Lyric {

    @Id
    @Schema(description = "歌词唯一ID（Mongo ObjectId）", example = "65e1c9c5e4b0f23a9c0a1234")
    private String id;

    @Indexed
    @Schema(description = "歌曲名称", example = "晴天", requiredMode = Schema.RequiredMode.REQUIRED)
    private String songName;

    @Schema(description = "歌手名称", example = "周杰伦", requiredMode = Schema.RequiredMode.REQUIRED)
    private String singer;

    @Schema(description = "所属专辑", example = "叶惠美")
    private String album;

    @Schema(description = "歌曲语言（zh/en/jp等）", example = "zh")
    private String language;

    @Schema(description = "歌词来源（QQ/网易/自制）", example = "网易")
    private String source;

    @Schema(description = "时间轴歌词列表")
    private List<LyricLine> lines;

    @Schema(description = "歌词总行数", example = "120")
    private Integer lineCount;

    @Schema(description = "歌曲总时长（秒）", example = "260")
    private Long duration;

    @Schema(description = "创建时间", example = "2026-03-02T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2026-03-02T10:20:30")
    private LocalDateTime updatedAt;

}