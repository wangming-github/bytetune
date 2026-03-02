package com.maizi.bytetune.starter.controller;

import com.maizi.bytetune.common.entity.Song;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
@Tag(name = "Song API", description = "歌曲管理接口")
public class SongController {

    @GetMapping("/{id}")
    @Operation(
            summary = "根据ID获取歌曲信息",
            description = "通过数据库主键ID查询歌曲",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "查询成功",
                            content = @Content(schema = @Schema(implementation = Song.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "歌曲不存在")
            }
    )
    public Song getSong(
            @Parameter(description = "歌曲ID", example = "1", required = true)
            @PathVariable Long id) {

        return Song.builder().build(); // 示例
    }

    @PostMapping
    @Operation(
            summary = "新增歌曲",
            description = "创建新的歌曲记录",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "创建成功",
                            content = @Content(schema = @Schema(implementation = Song.class))
                    )
            }
    )
    public Song createSong(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "歌曲实体",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Song.class))
            )
            @RequestBody Song song) {

        return song;
    }
}