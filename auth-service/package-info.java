/**
 * auth-service
 * ├── auth-api      # 接口 DTO / REST API
 * ├── auth-core     # 核心业务逻辑 Service
 * ├── auth-domain   # 实体类、Mapper、Repository
 * ├── auth-starter  # 自动配置 / 启动器 / 测试
 * ├── auth-util     # 工具类 / 公共方法
 * <p>
 * TODO 修改依赖
 * 模块依赖方向：auth-starter -> auth-api -> auth-core -> auth-domain / auth-util
 * <p>
 * 关键原则
 * •	API 不写业务
 * •	Core 不直接写 SQL
 * •	Domain 不写流程编排
 * •	Starter 不写数据库逻辑
 * •	Util 不依赖 Spring Bean
 */
package com.maizi.bytetune.common.dto;