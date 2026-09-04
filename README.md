# ByteTune

ByteTune 是一个基于 **Java 17** 和 **Spring Boot 3.3.x** 的个人音乐库管理系统。它将本地音频文件的扫描、元数据提取、歌曲建档和对象存储拆分为多个服务，并通过 Kafka 以事件方式串联。

> 项目目前处于服务拆分后的迭代阶段。本文以当前代码结构为准。

## 功能

- 监听或扫描本地音乐目录，识别音频文件
- 支持 NCM 文件转换，并提取音频元数据
- 根据文件信息创建歌曲记录，使用 MD5 协助识别重复文件
- 将音频上传至 MinIO，并把存储位置回写到歌曲记录
- 提供歌曲、歌词和认证相关的 HTTP API
- 使用 MySQL 保存歌曲与认证数据，MongoDB 保存歌词
- 使用 Kafka 承载服务间异步事件
- 提供 OpenAPI / Swagger UI、Actuator、Druid 与 Prometheus 相关观测能力

## 架构与处理流程

```text
本地音乐目录
     │
     ▼
bytetune-file-service ── file-to-song ──► bytetune-song-service
                                                  │
                                          song-to-storage
                                                  │
                                                  ▼
                                      bytetune-storage-service
                                                  │
                                          storage-to-song
                                                  │
                                                  ▼
                                      bytetune-song-service（更新存储信息）
```

三个 Kafka topic 在 `bytetune-messaging` 中集中定义：`file-to-song`、`song-to-storage` 和 `storage-to-song`。跨服务消息 DTO 位于 `bytetune-contract`。

## 模块

| 模块 | 作用 |
| --- | --- |
| `bytetune-common` | 公共响应、异常处理、线程与 OpenAPI 配置 |
| `bytetune-contract` | 服务间事件契约与 DTO |
| `bytetune-messaging` | Kafka 生产、消费和错误处理配置 |
| `bytetune-file-service` | 文件扫描、目录监听、NCM 转换、音频解析与事件发布 |
| `bytetune-song-service` | 歌曲领域、MySQL 持久化、事件消费/生产与歌曲 API |
| `bytetune-storage-service` | MinIO 上传、存储事件消费与状态回传 |
| `bytetune-lyric-service` | 歌词上传、解析、查询与 MongoDB 持久化 |
| `bytetune-observe` | Actuator、Prometheus、链路追踪和 Druid 相关能力 |
| `auth-service` | JWT、Spring Security、Redis 与基于角色/权限的认证服务 |

## 技术栈

- Java 17、Maven、Spring Boot 3.3.5
- Spring Web、Spring Security、SpringDoc OpenAPI
- MyBatis-Plus、MySQL、Druid
- MongoDB
- Apache Kafka
- MinIO
- Apache Tika、FFmpeg / `ncmdump`
- Spring Boot Actuator、Micrometer、Prometheus、OpenTelemetry

## 前置条件

本地开发需要准备：

- JDK 17+
- Maven 3.9+
- MySQL（默认配置使用 `byte_tune` 数据库）
- MongoDB（歌词服务默认使用 `lyrics_db`）
- Kafka（默认 `localhost:9092`）
- MinIO（默认 `http://localhost:9000`，bucket 为 `songs`）
- 可选：`ncmdump`，仅在需要转换 NCM 文件时使用

配置文件位于各模块的 `src/main/resources/`。开始运行前，请按自己的环境调整数据库、Kafka、MinIO、Redis、目录路径及凭据；不要将生产凭据提交到仓库。

文件服务还需要配置下列属性：

```yaml
bytetune-file-service:
  watch-path-in: /path/to/source-music
  watch-path-out: /path/to/converted-music
  ncm-decoder-command: /path/to/ncmdump
```

## 构建

在仓库根目录执行：

```bash
mvn clean install
```

若只构建某个服务及其依赖，可使用 Maven 的 `-pl` 与 `-am` 参数，例如：

```bash
mvn -pl bytetune-song-service -am clean package
```

## 启动服务

先确保基础设施已启动，再在仓库根目录按需启动各服务：

```bash
mvn -pl bytetune-song-service -am spring-boot:run
mvn -pl bytetune-file-service -am spring-boot:run
mvn -pl bytetune-storage-service -am spring-boot:run
mvn -pl bytetune-lyric-service -am spring-boot:run
mvn -pl auth-service/auth-starter -am spring-boot:run
```

| 服务 | 默认端口 | 说明 |
| --- | ---: | --- |
| `bytetune-song-service` | 8080 | 歌曲 API 和歌曲事件处理 |
| `bytetune-file-service` | 8081 | 文件扫描与目录监听 |
| `bytetune-lyric-service` | 8082 | 歌词 API |
| `bytetune-storage-service` | 8083 | MinIO 上传与存储事件处理 |
| `auth-service/auth-starter` | 8089 | 认证 API |

## API

歌曲服务默认地址为 `http://localhost:8080`，并提供：

- OpenAPI JSON：`/v3/api-docs`
- Swagger UI：`/swagger-ui.html`
- ReDoc：`/redoc/api.html`

当前主要路由包括：

| 服务 | 路由 |
| --- | --- |
| 歌曲服务 | `GET /songs/{id}`、`POST /songs` |
| 歌词服务 | `POST /lyrics`、`GET /lyrics`、`GET /lyrics/all`、`POST /lyrics/upload` |
| 认证服务 | `POST /auth/login` |

具体请求和响应模型以各服务的 OpenAPI 文档及控制器实现为准。

## 开发说明

- 根 `pom.xml` 是 Maven 聚合与版本管理入口。
- 服务间不要直接依赖彼此的实现；共享事件应添加到 `bytetune-contract`。
- 新增 Kafka topic 或消费者组时，请同步更新 `bytetune-messaging` 的配置。
- 配置文件当前以本地开发为主；在部署环境中建议通过环境变量、配置中心或外部配置覆盖敏感属性。

## 许可证

暂未声明许可证。
