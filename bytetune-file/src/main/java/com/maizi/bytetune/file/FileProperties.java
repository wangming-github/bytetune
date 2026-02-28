package com.maizi.bytetune.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bytetune")
public class FileProperties {
    /**
     * 扫描目录
     */
    private String watchPath;
}
