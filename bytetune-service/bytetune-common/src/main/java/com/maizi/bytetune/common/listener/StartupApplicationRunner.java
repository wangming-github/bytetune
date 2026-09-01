package com.maizi.bytetune.common.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 需要处理 Spring Boot 命令行参数 → ApplicationRunner
 * 自己处理
 * Spring Boot 帮你解析
 */
@Slf4j
@Component
public class StartupApplicationRunner implements ApplicationRunner {

    /*
     * 启动：
     * java -jar bytetune-service.jar \
     * --scan=/Users/zimai/Music \
     * --mode=full \
     * --debug=true （或者无值模式--debug）
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("⭕ApplicationArguments 执行");
        String scan = getOption(args, "scan");
        String mode = getOption(args, "mode");
        boolean debug = args.containsOption("debug");

        logIfNotBlank("扫描目录", scan);
        logIfNotBlank("运行模式", mode);
        if (debug) {
            log.info("Debug 模式 = true");
        }
    }

    /*日志大概：
     * ApplicationRunner 开始执行
     * 扫描目录 = /Users/zimai/Music
     * 运行模式 = full
     * Debug 模式 =true
     */

    private String getOption(ApplicationArguments args, String name) {
        List<String> values = args.getOptionValues(name);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private void logIfNotBlank(String name, String value) {
        if (value != null && !value.isBlank()) {
            log.info("{} = {}", name, value);
        }
    }
}