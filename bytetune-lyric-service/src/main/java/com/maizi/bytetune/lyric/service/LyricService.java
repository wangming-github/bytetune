package com.maizi.bytetune.lyric.service;

import com.maizi.bytetune.lyric.entity.Lyric;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LyricService {

    Lyric save(Lyric lyric);

    Lyric saveFile(MultipartFile file);

    Lyric getBySong(String songName, String singer);

    void delete(String id);

    List<Lyric> list();
}
