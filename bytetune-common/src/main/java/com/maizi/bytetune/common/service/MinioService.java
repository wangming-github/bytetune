package com.maizi.bytetune.common.service;

import com.maizi.bytetune.common.entity.Song;

public interface MinioService {
    boolean uploadToMinio(Song song, String objectName);
}
