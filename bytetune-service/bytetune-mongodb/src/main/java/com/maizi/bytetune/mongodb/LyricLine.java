package com.maizi.bytetune.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LyricLine", description = "单行时间轴歌词")
public class LyricLine {

    @Schema(description = "时间轴时间戳（毫秒）", example = "15320", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long time;

    @Schema(description = "歌词文本内容", example = "故事的小黄花，从出生那年就飘着", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;
}