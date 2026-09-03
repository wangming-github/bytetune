package com.maizi.bytetune.file.decoder;

import java.nio.file.Path;
import java.util.List;

public interface NcmDecoder {

    /**
     * 解密 NCM 文件，并返回解密后的音乐文件
     */
    List<Path> decode(Path ncmFile);
}