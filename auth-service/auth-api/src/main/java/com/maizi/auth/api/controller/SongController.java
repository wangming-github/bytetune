package com.maizi.auth.api.controller;

import com.maizi.auth.util.CheckPermission;
import com.maizi.auth.util.PermissionConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 示例 Controller
@RestController
@RequestMapping("/songs")
public class SongController {

    // 查询接口
    // 不加注解 = 只要登录即可访问（依赖 SecurityConfig 的 authenticated()）
    @GetMapping
    public List<String> listSongs() {
        return List.of("song1", "song2");
    }

    // 删除接口
    // 必须具备权限：song:delete
    @PreAuthorize("hasAuthority('song:delete')")
    @CheckPermission(PermissionConstants.SONG_DELETE)
    @DeleteMapping("/{id}")
    public String deleteSong(@PathVariable Long id) {
        return "删除成功：" + id;
    }

    // 只有 ADMIN 角色能访问
    // 注意：hasRole 会自动加前缀 ROLE_
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String addSong() {
        return "新增成功";
    }
}