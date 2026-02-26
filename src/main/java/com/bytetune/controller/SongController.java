package com.bytetune.controller;

import com.bytetune.entity.Song;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
public class SongController {

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取歌曲信息")
    public Song getSong(@PathVariable Long id) {
        return new Song(); // 示例返回
    }

    @PostMapping
    @Operation(summary = "新增歌曲")
    public Song createSong(@RequestBody Song song) {
        return song;
    }
}
