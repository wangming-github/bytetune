所有业务服务都可能复用的“无业务倾向”的基础 Java 库。

common 不应该知道
```shell

Song
KafkaSongEvent
SongMapper
SongService
文件扫描
MinIO 上传
歌词
数据库业务
```
common 可以知道：
```shell
R
异常体系
通用注解
通用工具
通用 DTO
通用常量
通用基础配置
```