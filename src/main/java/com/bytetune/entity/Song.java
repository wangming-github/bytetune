package com.bytetune.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "歌曲实体")
public class Song {

    @Schema(description = "歌曲ID", example = "1")
    private Long id;

    @Schema(description = "歌曲名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夜曲")
    private String name;

    @Schema(description = "歌手名称", example = "周杰伦")
    private String artist;

    @Schema(description = "专辑名称", example = "十一月的萧邦")
    private String album;

    @Schema(description = "歌曲时长（秒）", example = "260")
    private Integer duration;

    @Schema(description = "音频文件 URL", example = "http://localhost:9000/music/yequ.mp3")
    private String fileUrl;

    @Schema(description = "音频文件类型", example = "audio/mpeg")
    private String fileType;

    @Schema(description = "封面 URL", example = "http://localhost:9000/cover/yequ.jpg")
    private String coverUrl;
}