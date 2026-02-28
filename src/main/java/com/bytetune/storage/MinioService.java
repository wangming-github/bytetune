package com.bytetune.storage;

import com.bytetune.entity.Song;

public interface MinioService {
    boolean uploadToMinio(Song song, String objectName);
}
