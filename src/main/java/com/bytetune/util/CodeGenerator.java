package com.bytetune.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器工具类
 *
 * <p>功能：
 * <ul>
 *     <li>根据数据库表自动生成 Entity、Mapper、Service、ServiceImpl 类</li>
 *     <li>生成 Mapper XML 文件</li>
 *     <li>支持 Lombok 注解、Swagger 注释生成</li>
 * </ul>
 *
 * <p>使用前请修改数据库连接信息、生成路径和表名。
 */
public class CodeGenerator {

    /**
     * 主方法，执行代码生成
     *
     * <p>说明：
     * 1. 配置数据库连接信息
     * 2. 配置全局生成策略（作者、Swagger、输出路径、注释日期）
     * 3. 配置包名及 Mapper XML 路径
     * 4. 配置表名、实体策略、逻辑删除、链式模型等
     * 5. 使用 Freemarker 模板生成代码
     *
     * @param args 命令行参数（可为空）
     */
    public static void main(String[] args) {
        // 数据库连接 URL，请根据实际情况修改
        String url = "jdbc:mysql://localhost:3306/byte_tune?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&remarks=true&useInformationSchema=true";

        FastAutoGenerator.create(url, "root", "admin123")
                // 全局配置
                .globalConfig(builder -> builder
                        .author("maizi")               // 作者信息
                        .enableSwagger()               // 开启 Swagger 注解生成
                        .outputDir("/Users/zimai/Desktop/byte_tune") // 生成路径
                        .commentDate("yyyy-MM-dd")     // 注释日期格式
                )
                // 包配置
                .packageConfig(builder -> builder
                        .parent("com.bytetune")        // 父包
                        .entity("entity")              // 实体类包名
                        .mapper("mapper")              // Mapper 接口包名
                        .service("service")            // Service 接口包名
                        .serviceImpl("service.impl")   // ServiceImpl 包名
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                "/Users/zimai/Desktop/byte_tune/mapper" // Mapper XML 生成路径
                        ))
                )
                // 策略配置
                .strategyConfig(builder -> builder
                        .addInclude("song")            // 需要生成的表
                        .addTablePrefix("")            // 去掉表前缀
                        .entityBuilder()
                        .enableLombok()                // 启用 Lombok 注解
                        .logicDeleteColumnName("deleted") // 逻辑删除字段
                        .enableChainModel()            // 链式模型
                        .mapperBuilder()
                        .build()
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板
                .execute(); // 执行生成
    }
}