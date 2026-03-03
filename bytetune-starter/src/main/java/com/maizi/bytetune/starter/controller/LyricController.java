package com.maizi.bytetune.starter.controller;

import com.maizi.bytetune.common.util.R;
import com.maizi.bytetune.mongodb.Lyric;
import com.maizi.bytetune.mongodb.LyricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lyrics")
@RequiredArgsConstructor
@Tag(name = "Lyric API", description = "歌词管理接口")
public class LyricController {

    private final LyricService lyricService;

    @PostMapping
    @Operation(summary = "创建歌词", description = "新增一首歌曲的时间轴歌词", responses = {@ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = Lyric.class)))})
    public Lyric create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "歌词实体", required = true, content = @Content(schema = @Schema(implementation = Lyric.class))) @RequestBody Lyric lyric) {
        return lyricService.save(lyric);
    }

    @GetMapping
    @Operation(summary = "根据歌曲名和歌手查询歌词")
    public Lyric get(@Parameter(description = "歌曲名称", example = "晴天", required = true) @RequestParam String songName,

                     @Parameter(description = "歌手名称", example = "周杰伦", required = true) @RequestParam String singer) {

        return lyricService.getBySong(songName, singer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除歌词")
    public void delete(@Parameter(description = "MongoDB ObjectId", example = "65e1c9c5e4b0f23a9c0a1234", required = true) @PathVariable String id) {
        lyricService.delete(id);
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部歌词")
    public List<Lyric> list() {
        return lyricService.list();
    }

    @PostMapping("/upload")
    @Operation(//
            summary = "上传歌词文件", //
            description = "通过上传 .lrc 文件自动解析歌词内容，并根据文件名自动提取歌手和歌曲名。文件名必须形如 '歌手 - 歌曲名.lrc'，返回 Lyric 对象并存入 MongoDB。")
    @ApiResponses({//
            @ApiResponse(responseCode = "200", description = "上传成功，返回解析后的 Lyric 对象"), //
            @ApiResponse(responseCode = "400", description = "文件名格式不正确或文件解析失败"),//
            @ApiResponse(responseCode = "500", description = "服务器内部错误")})
    public R<Lyric> upload(@Parameter(description = "要上传的歌词文件（.lrc）", required = true) //
                           @RequestParam("file") MultipartFile file) {
        Lyric lyric = lyricService.saveFile(file);
        return R.ok(lyric);
    }
}