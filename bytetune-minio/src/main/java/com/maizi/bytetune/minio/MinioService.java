package com.maizi.bytetune.minio;

import com.maizi.bytetune.common.entity.Song;

public interface MinioService {
    boolean uploadToMinio(Song song, String objectName);

    void uploadToMinioAndUpdateState(Song song);
}
