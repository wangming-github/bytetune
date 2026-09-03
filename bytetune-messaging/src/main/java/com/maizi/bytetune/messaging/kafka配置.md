### 如果你这是开发环境第一次初始化 / 想彻底重置 Kafka。
- 1.先进入 Kafka：`cd /Users/zimai/Documents/dev/env/kafka/kafka_2.13-4.2.0`
- 2.如果你这是本地开发环境，之前的数据不要了，可以直接清空 Kafka 数据目录：`rm -rf /tmp/kraft-combined-logs`
  - ⚠️ `rm -rf /tmp/kraft-combined-logs`会删除这个 Kafka 实例现有的数据(创建的 topic、消息、offset 等数据)。
- 3.然后生成 Cluster ID：`bin/kafka-storage.sh random-uuid` 会得到类似：`MkU3OEVBNTcwNTJENDM2Qk`
- 4.复制这个 ID，然后执行：`bin/kafka-storage.sh format -t MkU3OEVBNTcwNTJENDM2Qk  -c config/server.properties `
- 5.看到类似：Formatting metadata directory /tmp/kraft-combined-logs就说明初始化成功。
- 6.然后启动：`bin/kafka-server-start.sh config/server.properties`
- 7.如果你只是想启动 Kafka，不占着终端：`bin/kafka-server-start.sh -daemon config/server.properties`。 
- 8.启动后检查：`jps`正常应该能看到类似： Kafka。

综上，直接执行：
```shell
cd /Users/zimai/Documents/dev/env/kafka/kafka_2.13-4.2.0

rm -rf /tmp/kraft-combined-logs

CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)

bin/kafka-storage.sh format \
  --standalone \
  -t "$CLUSTER_ID" \
  -c config/server.properties

bin/kafka-server-start.sh config/server.properties
```
