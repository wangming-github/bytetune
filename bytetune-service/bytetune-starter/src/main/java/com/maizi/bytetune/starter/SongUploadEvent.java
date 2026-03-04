package com.maizi.bytetune.starter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongUploadEvent {
    private Long songId;
    private String filePath;
}