package com.bytetune.storage;

import com.bytetune.entity.Song;

public interface MinioService {
    void uploadToMinio(Song song, String objectName);
}
