package com.maizi.ncm2mp3;

import com.bytetune.util.SongFileScanner;

public class TestScanner {
    public static void main(String[] args) throws Exception {
        String folder = "/Users/zimai/Music/云音乐转换mp3"; // 本地音乐目录
        var songs = SongFileScanner.scanFolder(folder);
        songs.forEach(System.out::println);
    }
}