package com.bytetune;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bytetune.mapper") // 避免 @ComponentScan("com.bytetune") 把 Mapper 扫描成普通 Bean
public class byteTuneApplication {

    public static void main(String[] args) {
        SpringApplication.run(byteTuneApplication.class, args);
    }

}
