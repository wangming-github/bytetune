
# 项目流程与扩展说明

## 项目主要流程

```azure



[启动项目]
       │
       ▼
[CommandLineRunner 执行 StartupScanner.scanLocalMusic()]
       │
       ▼
[扫描本地音乐目录 SongFileScanner]
       │
       ▼
[转换 SongFileInfo → Song（SongFileScannerExt）]
       │
       ▼
[去重：数据库中是否存在 path+md5（ISongService.existsByFile）]
       │
       ├─> 已存在 → 跳过
       │
       ▼
[批量入库数据库（SongService.saveBatch）]
       │
       ▼
[定时任务 SongUploadScheduler.uploadUnprocessedSongs]
       │
       ▼
[查询 status=0 的 Song 列表]
       │
       ▼
[上传 MinIO（MinioService.uploadToMinio）]
       │
       ├─> 成功 → 更新 status=1 + bucketName + objectName
       │
       └─> 失败 → 更新 status=2
       │
       ▼
[日志记录]

扩展说明
    1.实时新增文件监听
        •FolderWatcher.watchFolder 监听目录
        •新文件触发 StartupScanner.handleNewFile
        •文件转 Song 并加入缓冲区
        •缓冲区达到 BATCH_SIZE 或定时触发 → 批量入库
    2.状态管理
        •status = 0 → 未上传
        •status = 1 → 上传成功
        •status = 2 → 上传失败
    3.MinIO对象命名规则
        •audio/{artist}/{album}/{songId}_{songName}.mp3
        •上传成功后 bucketName + objectName 填充到 Song 表
    4.定时批量提交
        •每 3 秒定时 flush 缓冲区
        •每 60 秒定时上传未处理文件

``` 
