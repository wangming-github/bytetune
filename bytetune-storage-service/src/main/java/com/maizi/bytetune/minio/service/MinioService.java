package com.maizi.bytetune.minio.service;

import com.maizi.bytetune.contract.event.storage.SongToStorageEventDto;
import io.minio.ObjectWriteResponse;

public interface MinioService {

    ObjectWriteResponse uploadToMinio(SongToStorageEventDto song, String fileName);

}
