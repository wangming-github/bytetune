# Getting Started
可以。下面这份更适合直接作为 `README.md` 里的 **Getting Started**，按照你现在 ByteTune 的 Maven 多模块结构来写，同时把 Kafka、MongoDB、MinIO、MySQL、启动参数这些都串起来。

# ByteTune

ByteTune 是一个基于 **Java 17 + Spring Boot 3.3.x** 构建的个人音乐管理与处理系统。

项目主要用于管理本地音乐文件，完成音乐文件扫描、元数据解析、对象存储、歌词管理以及异步任务处理等功能。

项目采用 Maven 多模块架构，并逐步引入 Kafka、MongoDB、MinIO 等基础设施。

---

## ✨ Features

* 🎵 本地音乐文件扫描
* 🏷️ MP3 音频元数据解析
* 🔍 文件 MD5 计算与重复文件识别
* 💾 MySQL 音乐数据持久化
* 🪣 MinIO 音频文件对象存储
* 📝 MongoDB 歌词存储
* 📨 Kafka 异步消息处理
* 📊 Druid 数据源监控
* 📚 OpenAPI / Swagger API 文档
* ⚙️ Spring Boot 多模块架构
* 🚀 支持命令行参数启动
* 📋 启动生命周期日志输出

---

# 🏗️ Project Structure

```text
bytetune
├── auth-service
│   ├── auth-api
│   ├── auth-core
│   ├── auth-domain
│   ├── auth-starter
│   ├── auth-util
│   └── pom.xml
│
├── bytetune-starter
│   ├── bytetune-common
│   ├── bytetune-file
│   ├── bytetune-minio
│   ├── bytetune-mongodb
│   ├── bytetune-observe
│   ├── bytetune-service
│   └── pom.xml
│
├── pom.xml
└── README.md
```

## Module Description

| Module             | Description         |
| ------------------ | ------------------- |
| `bytetune-common`  | 公共工具、通用配置、基础组件      |
| `bytetune-file`    | 文件扫描、文件信息解析、音频元数据处理 |
| `bytetune-minio`   | MinIO 对象存储          |
| `bytetune-mongodb` | MongoDB 数据访问、歌词管理   |
| `bytetune-observe` | 日志、监控、运行状态等基础能力     |
| `bytetune-service` | ByteTune 主服务启动模块    |
| `bytetune-starter` | ByteTune 基础模块父工程    |
| `auth-api`         | 认证相关 API            |
| `auth-core`        | 认证核心业务              |
| `auth-domain`      | 认证领域模型              |
| `auth-starter`     | 认证服务启动模块            |
| `auth-util`        | 认证相关工具              |

---

# 🛠️ Tech Stack

## Runtime

* Java 17
* Spring Boot 3.3.x
* Maven

## Database

* MySQL
* MongoDB

## Middleware

* Apache Kafka
* MinIO

## File Processing

* Apache Tika
* FFmpeg

## Monitoring

* Druid
* Logback
* Spring Boot Actuator

## API

* Spring Web
* SpringDoc OpenAPI

---

# 📋 Requirements

运行 ByteTune 前，需要准备：

```text
Java 17+
Maven 3.9+
MySQL
MongoDB
Kafka
MinIO
```

检查 Java：

```bash
java -version
```

应该看到类似：

```text
openjdk version "17.x.x"
```

检查 Maven：

```bash
mvn -version
```

---

# 🚀 Getting Started

## 1. Clone

```bash
git clone <your-repository-url>

cd bytetune
```

---

## 2. Build

在项目根目录执行：

```bash
mvn clean install
```

如果构建成功：

```text
BUILD SUCCESS
```

Maven 会按照模块依赖关系自动构建：

```text
bytetune
  ↓
bytetune-common
  ↓
bytetune-file
  ↓
bytetune-minio
  ↓
bytetune-mongodb
  ↓
bytetune-observe
  ↓
bytetune-service
```

---

# 🗄️ Infrastructure

ByteTune 当前依赖以下基础设施。

```text
                    ┌──────────────┐
                    │   ByteTune   │
                    │   Service    │
                    └──────┬───────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
       MySQL           MongoDB            MinIO
          │                │                │
      音乐数据          歌词数据          音频文件
                           │
                           ▼
                         Kafka
                           │
                      异步任务处理
```

---

# 🐬 MySQL

创建 ByteTune 数据库：

```sql
CREATE DATABASE bytetune
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

然后配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bytetune?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your-password
```

---

# 🍃 MongoDB

启动 MongoDB：

```bash
brew services start mongodb-community
```

或者：

```bash
mongod --dbpath ~/data/mongodb
```

默认连接地址：

```text
mongodb://localhost:27017
```

配置：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bytetune
```

---

# 🪣 MinIO

启动 MinIO：

```bash
minio server ~/minio-data
```

默认 API：

```text
http://127.0.0.1:9000
```

MinIO Console 地址根据启动配置确定。

配置：

```yaml
minio:
  endpoint: http://127.0.0.1:9000
  access-key: minioadmin
  secret-key: minioadmin
```

> 实际生产环境不要使用默认账号密码。

---

# 📨 Kafka

ByteTune 使用 Kafka 处理异步任务。

Kafka 4.x 使用 KRaft 模式，不再依赖 ZooKeeper。

进入 Kafka：

```bash
cd /path/to/kafka_2.13-4.2.0
```

首次初始化：

```bash
rm -rf /tmp/kraft-combined-logs

CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)

bin/kafka-storage.sh format \
  --standalone \
  -t "$CLUSTER_ID" \
  -c config/server.properties
```

启动：

```bash
bin/kafka-server-start.sh config/server.properties
```

后台启动：

```bash
nohup bin/kafka-server-start.sh config/server.properties \
  > /tmp/kafka.log 2>&1 &
```

检查 Kafka：

```bash
jps
```

或者：

```bash
lsof -i :9092
```

ByteTune 默认连接：

```text
localhost:9092
```

---

# ⚙️ Configuration

主要配置文件：

```text
bytetune-service
└── src
    └── main
        └── resources
            ├── application.yml
            └── logback-spring.xml
```

推荐将本地环境配置独立出来：

```text
application.yml
application-local.yml
application-prod.yml
```

例如：

```yaml
spring:
  profiles:
    active: local
```

---

# 🎵 Music Scan

ByteTune 支持通过启动参数指定扫描目录。

例如：

```bash
java -jar bytetune-service.jar \
  --scan=/Users/zimai/Music \
  --mode=full \
  --debug
```

参数说明：

| Parameter | Description   |
| --------- | ------------- |
| `--scan`  | 音乐扫描目录        |
| `--mode`  | 扫描模式          |
| `--debug` | 是否启用 Debug 模式 |

例如：

```bash
--scan=/Users/zimai/Music
```

表示扫描：

```text
/Users/zimai/Music
```

---

# ▶️ Run From Maven

开发阶段可以直接运行：

```bash
mvn spring-boot:run
```

也可以指定参数：

```bash
mvn spring-boot:run \
  -Dspring-boot.run.arguments="--scan=/Users/zimai/Music --mode=full --debug"
```

---

# 📦 Build Executable JAR

构建：

```bash
mvn clean package
```

构建完成后：

```text
bytetune-service/
└── target/
    └── bytetune-service-0.0.1-SNAPSHOT.jar
```

启动：

```bash
java -jar target/bytetune-service-0.0.1-SNAPSHOT.jar
```

带参数：

```bash
java -jar target/bytetune-service-0.0.1-SNAPSHOT.jar \
  --scan=/Users/zimai/Music \
  --mode=full \
  --debug
```

---

# 📨 Kafka Async Processing

ByteTune 中耗时任务可以通过 Kafka 异步处理。

典型流程：

```text
扫描音乐文件
      │
      ▼
保存音乐信息
      │
      ▼
产生 SongUploadEvent
      │
      ▼
Kafka Topic
      │
      ▼
Kafka Consumer
      │
      ▼
上传 MinIO
      │
      ▼
更新数据库状态
```

例如：

```java
public record SongUploadEvent(
        Long songId,
        String bucketName,
        String objectName,
        String filePath,
        Long timestamp,
        Integer retryCount
) {
}
```

这样可以避免文件扫描线程直接阻塞在对象存储上传操作上。

---

# 📊 Monitoring

启动 ByteTune 后，可以访问：

### OpenAPI

```text
http://localhost:8080/redoc/api.html
```

### Druid

```text
http://localhost:8080/druid/index.html
```

### MinIO

```text
http://127.0.0.1:9000
```

---

# 📝 Logging

日志默认输出到：

```text
/Users/zimai/Documents/dev/env/log/lbytetune/
```

例如：

```text
lbytetune/
├── bytetune.log
└── archived/
    ├── bytetune.2026-03-04.0.log.gz
    ├── bytetune.2026-03-04.1.log.gz
    └── ...
```

日志按照：

```text
日期 + 文件大小
```

进行滚动。

默认：

```text
单文件最大：10 MB
日志保留：30 天
总大小限制：1 GB
```

---

# 🔄 Application Startup Lifecycle

ByteTune 对 Spring Boot 启动生命周期进行了日志化。

典型启动过程：

```text
SpringApplication
      │
      ▼
ApplicationStartingEvent
      │
      ▼
EnvironmentPreparedEvent
      │
      ▼
ApplicationContextInitializedEvent
      │
      ▼
ApplicationPreparedEvent
      │
      ▼
ContextRefreshedEvent
      │
      ▼
ApplicationStartedEvent
      │
      ▼
CommandLineRunner
      │
      ▼
ApplicationRunner
      │
      ▼
ApplicationReadyEvent
```

最终：

```text
ByteTune Started
```

表示应用已经完成启动，可以接受正常业务请求。

---

# 🧪 Testing

运行全部测试：

```bash
mvn test
```

运行指定模块：

```bash
cd bytetune-starter/bytetune-service

mvn test
```

或者：

```bash
mvn -pl bytetune-starter/bytetune-service test
```

---

# 🧹 Clean

清理所有模块：

```bash
mvn clean
```

重新构建：

```bash
mvn clean install
```

---

# 🐛 Debug

开发环境建议：

```bash
java -jar bytetune-service.jar \
  --scan=/Users/zimai/Music \
  --mode=full \
  --debug
```

也可以通过 IDEA 直接启动 `ByteTuneApplication`。

---

# 📌 Development Workflow

推荐开发流程：

```text
1. 启动 MySQL
       ↓
2. 启动 MongoDB
       ↓
3. 启动 MinIO
       ↓
4. 启动 Kafka
       ↓
5. 启动 ByteTune
       ↓
6. 扫描音乐
       ↓
7. 写入 MySQL
       ↓
8. Kafka 异步任务
       ↓
9. 上传 MinIO
       ↓
10. 歌词写入 MongoDB
```

---

# 🧱 Architecture

整体架构：

```text
                    ┌────────────────────┐
                    │    ByteTune API    │
                    └─────────┬──────────┘
                              │
                              ▼
                    ┌────────────────────┐
                    │  bytetune-service  │
                    └─────────┬──────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
            ▼                 ▼                 ▼
      bytetune-file     bytetune-minio   bytetune-mongodb
            │                 │                 │
            ▼                 ▼                 ▼
       Local Files          MinIO           MongoDB
            │
            ▼
        MySQL Metadata
            │
            ▼
          Kafka
            │
            ▼
       Async Processing
```

---

# 📈 Roadmap

* [ ] 音乐文件扫描
* [ ] MP3 Metadata 解析
* [ ] MD5 去重
* [ ] MySQL 音乐数据库
* [ ] MinIO 音频存储
* [ ] MongoDB 歌词存储
* [ ] Kafka 异步任务
* [ ] 文件上传重试机制
* [ ] 歌词自动匹配
* [ ] 音乐搜索
* [ ] 音乐播放 API
* [ ] Web Player
* [ ] 用户认证
* [ ] RBAC 权限管理
* [ ] OpenAPI 完整接口
* [ ] Docker Compose
* [ ] Kubernetes 部署
* [ ] Prometheus + Grafana 监控

---

# 📄 License

This project is for personal learning and development.

---

````

### 我建议你再补一块

你这个项目已经不是单纯的 Spring Boot Demo 了，README 最好后面继续加一个 **Architecture / Design** 章节，把：

```text
bytetune
├── starter
│   ├── common
│   ├── file
│   ├── minio
│   ├── mongodb
│   ├── observe
│   └── service
│
└── auth-service
    ├── api
    ├── core
    ├── domain
    ├── starter
    └── util
````

以及 **模块依赖关系、Kafka 消息流、数据库关系、启动生命周期** 单独画成 Mermaid 图。

这样这个 GitHub 项目看起来就会从“几个 Spring Boot 模块拼起来的项目”，变成一个**有完整架构设计的 Java 项目**。
