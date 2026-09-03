package com.maizi.bytetune.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bytetune-file-service")
public class FileServiceProperties {
    /**
     * 扫描目录-输入
     */
    private String watchPathIn;
    /**
     * 扫描目录-输出
     */
    private String watchPathOut;
    /**
     * NCM 解密程序路径
     */
    private String ncmDecoderCommand;
}
