package com.maizi.bytetune.file.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 简单执行启动任务 → CommandLineRunner
 * 自己处理
 * 两者直接对比
 * CommandLineRunner	ApplicationRunner
 * 参数	String... args	ApplicationArguments
 * 参数解析	自己处理	Spring Boot 帮你解析
 * --name=maizi	字符串	可以直接获取 name
 * 普通参数	字符串	getNonOptionArgs()
 * 使用难度	简单	稍复杂
 * 适合	简单启动任务	需要解析启动参数
 */
@Slf4j
@Component
public class StartupCommandLineRunner implements CommandLineRunner {

    /*
     * 启动： java -jar bytetune-service.jar --server.port=8081 test hello
     */
    @Override
    public void run(String... args) {
        log.debug("⭕CommandLineRunner 执行");
        for (String arg : args) {
            log.info("参数：{}", arg);
        }
    }

    /*
      输出：
      CommandLineRunner 执行
      参数：--server.port=8081
      参数：test
      参数：hello
     */
}