# Redis学习教程 - 第3章：Redis常用命令详解

## 🎯 本章目标
- 系统掌握Redis各类命令的使用方法
- 理解命令的执行原理和性能特点
- 学会组合使用命令解决实际问题
- 掌握命令的监控和调试技巧

## 📋 通用命令

### 键管理命令
```bash
# 键的基本操作
EXISTS key                           # 检查键是否存在
DEL key [key ...]                    # 删除一个或多个键
TYPE key                             # 返回键的类型
RANDOMKEY                            # 随机返回一个键
RENAME key newkey                    # 重命名键
RENAMENX key newkey                  # 仅当新键不存在时重命名

# 键的过期时间
EXPIRE key seconds                   # 设置过期时间（秒）
PEXPIRE key milliseconds             # 设置过期时间（毫秒）
EXPIREAT key timestamp               # 设置过期时间戳（秒）
PEXPIREAT key timestamp              # 设置过期时间戳（毫秒）
TTL key                              # 查看剩余生存时间（秒）
PTTL key                             # 查看剩余生存时间（毫秒）
PERSIST key                          # 移除过期时间

# 键的扫描
KEYS pattern                         # 查找匹配的键（不推荐生产环境使用）
SCAN cursor [MATCH pattern] [COUNT count]  # 增量式迭代键空间
```

### 服务器管理命令
```bash
# 服务器信息
PING [message]                       # 测试连接
ECHO message                         # 回显消息
INFO [section]                       # 获取服务器信息
TIME                                 # 返回当前服务器时间
CLIENT LIST                          # 列出连接的客户端
CLIENT KILL ip:port                  # 断开客户端连接

# 配置管理
CONFIG GET parameter                 # 获取配置参数
CONFIG SET parameter value           # 设置配置参数
CONFIG RESETSTAT                     # 重置统计信息

# 数据库操作
SELECT index                         # 选择数据库（0-15）
FLUSHDB                              # 清空当前数据库
FLUSHALL                             # 清空所有数据库
DBSIZE                               # 返回当前数据库键的数量
LASTSAVE                             # 返回最后一次保存时间
SAVE                                 # 同步保存数据到磁盘
BGSAVE                               # 异步保存数据到磁盘
SHUTDOWN [NOSAVE|SAVE]               # 关闭服务器
```

## 🔤 String相关命令详解

### 基础操作命令
```bash
# 设置和获取
SET key value [EX seconds] [PX milliseconds] [NX|XX]
GET key
GETSET key value                     # 设置新值并返回旧值
MGET key [key ...]                   # 批量获取
MSET key value [key value ...]       # 批量设置
MSETNX key value [key value ...]     # 批量设置（原子性）

# 数值操作
INCR key                             # 自增1
DECR key                             # 自减1
INCRBY key increment                 # 增加指定值
DECRBY key decrement                 # 减少指定值
INCRBYFLOAT key increment            # 浮点数增加

# 字符串操作
APPEND key value                     # 追加字符串
STRLEN key                           # 获取字符串长度
GETRANGE key start end               # 获取子字符串
SETRANGE key offset value            # 设置指定位置字符
BITCOUNT key [start end]             # 统计被设置为1的位数量
BITPOS key bit [start end]           # 查找第一个被设置为指定值的位
```

### 实战示例
```bash
# 用户登录计数器
SET login_count:2024-01-15 0
INCR login_count:2024-01-15
GET login_count:2024-01-15

# 文章阅读统计
INCR views:article:12345
INCR views:article:12345

# 限时优惠券
SET coupon:A1B2C3 "discount_20_percent" EX 3600 NX

# 用户在线状态
SETEX user:online:1001 300 "active"  # 5分钟后过期
```

## 📦 Hash相关命令详解

### 字段操作命令
```bash
# 基础操作
HSET key field value                 # 设置字段值
HGET key field                       # 获取字段值
HMSET key field value [field value ...]  # 设置多个字段
HMGET key field [field ...]          # 获取多个字段
HGETALL key                          # 获取所有字段和值
HEXISTS key field                    # 判断字段是否存在
HDEL key field [field ...]           # 删除字段
HLEN key                             # 获取字段数量

# 数值操作
HINCRBY key field increment          # 字段值整数增加
HINCRBYFLOAT key field increment     # 字段值浮点数增加

# 批量操作
HKEYS key                            # 获取所有字段名
HVALS key                            # 获取所有字段值
HSETNX key field value               # 仅当字段不存在时设置
HSTRLEN key field                    # 获取字段值长度
```

### 实战示例
```bash
# 用户信息管理
HMSET user:1001 name "张三" age 25 email "zhangsan@example.com" city "北京"
HGET user:1001 email
HINCRBY user:1001 age 1              # 生日增加年龄
HGETALL user:1001

# 购物车管理
HINCRBY cart:user:1001 product:2001 1    # 添加商品
HINCRBY cart:user:1001 product:2002 2    # 添加2个商品
HGETALL cart:user:1001
HDEL cart:user:1001 product:2001         # 删除商品

# 配置管理
HMSET system:config max_connections 100 timeout 30 debug_mode false
HGET system:config max_connections
```

## 📋 List相关命令详解

### 列表操作命令
```bash
# 添加元素
LPUSH key value [value ...]          # 左侧插入
RPUSH key value [value ...]          # 右侧插入
LPUSHX key value                     # 仅当列表存在时左侧插入
RPUSHX key value                     # 仅当列表存在时右侧插入

# 移除元素
LPOP key                             # 左侧弹出
RPOP key                             # 右侧弹出
BLPOP key [key ...] timeout          # 阻塞左侧弹出
BRPOP key [key ...] timeout          # 阻塞右侧弹出
RPOPLPUSH source destination         # 右侧弹出左侧插入
BRPOPLPUSH source destination timeout # 阻塞式RPOPLPUSH

# 查看元素
LRANGE key start stop                # 获取范围元素
LINDEX key index                     # 获取指定位置元素
LLEN key                             # 获取列表长度

# 修改元素
LSET key index value                 # 设置指定位置值
LINSERT key BEFORE|AFTER pivot value # 插入元素

# 删除元素
LREM key count value                 # 删除指定值的元素
LTRIM key start end                  # 保留指定范围元素
```

### 实战示例
```bash
# 消息队列实现
LPUSH message_queue "订单处理任务1"
LPUSH message_queue "订单处理任务2"
BRPOP message_queue 30               # 阻塞等待30秒

# 最新消息列表
LPUSH user:1001:news "用户发布了新动态"
LPUSH user:1001:news "用户点赞了某篇文章"
LTRIM user:1001:news 0 99            # 只保留最近100条

# 任务调度
LPUSH task_queue "高优先级任务"
LPUSH task_queue "普通任务"
RPOP task_queue                      # 处理任务（先进先出）
```

## 🔘 Set相关命令详解

### 集合操作命令
```bash
# 基础操作
SADD key member [member ...]         # 添加元素
SMEMBERS key                         # 获取所有元素
SISMEMBER key member                 # 判断元素是否存在
SCARD key                            # 获取集合大小
SREM key member [member ...]         # 删除元素
SPOP key [count]                     # 随机弹出元素
SRANDMEMBER key [count]              # 随机获取元素

# 集合运算
SINTER key [key ...]                 # 交集
SUNION key [key ...]                 # 并集
SDIFF key [key ...]                  # 差集
SINTERSTORE destination key [key ...] # 交集并存储
SUNIONSTORE destination key [key ...] # 并集并存储
SDIFFSTORE destination key [key ...]  # 差集并存储

# 迭代操作
SSCAN key cursor [MATCH pattern] [COUNT count]
```

### 实战示例
```bash
# 标签系统
SADD article:12345:tags "技术" "Java" "Redis"
SADD article:67890:tags "技术" "Python" "数据库"
SINTER article:12345:tags article:67890:tags  # 共同标签

# 好友推荐
SADD friends:user:1001 "user:1002" "user:1003" "user:1004"
SADD friends:user:1002 "user:1001" "user:1005" "user:1006"
SDIFF friends:user:1002 friends:user:1001  # 可能认识的人

# 权限管理
SADD role:admin "read" "write" "delete" "manage_users"
SADD role:editor "read" "write"
SISMEMBER role:admin "delete"  # 检查是否有删除权限
```

## 📊 Sorted Set相关命令详解

### 有序集合操作命令
```bash
# 基础操作
ZADD key [NX|XX] [CH] [INCR] score member [score member ...]
ZSCORE key member                    # 获取元素分数
ZCARD key                            # 获取集合大小
ZCOUNT key min max                   # 统计分数范围内元素数

# 排名操作
ZRANK key member                     # 获取元素排名（升序）
ZREVRANK key member                  # 获取元素排名（降序）
ZRANGE key start stop [WITHSCORES]   # 按排名获取元素（升序）
ZREVRANGE key start stop [WITHSCORES] # 按排名获取元素（降序）

# 分数操作
ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]
ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
ZREMRANGEBYRANK key start stop       # 按排名删除
ZREMRANGEBYSCORE key min max         # 按分数删除

# 增量操作
ZINCRBY key increment member         # 增加元素分数

# 集合运算
ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]

# 迭代操作
ZSCAN key cursor [MATCH pattern] [COUNT count]
```

### 实战示例
```bash
# 排行榜系统
ZADD leaderboard 1500 "user:1001"
ZADD leaderboard 1200 "user:1002"
ZADD leaderboard 1800 "user:1003"
ZREVRANGE leaderboard 0 9 WITHSCORES  # Top 10排行榜

# 获取用户排名
ZRANK leaderboard "user:1002"
ZSCORE leaderboard "user:1002"

# 积分变动
ZINCRBY leaderboard 100 "user:1001"   # 增加100分

# 时间轴排序
ZADD timeline:user:1001 1640995200 "status:001"
ZADD timeline:user:1001 1640995300 "status:002"
ZREVRANGE timeline:user:1001 0 9      # 获取最新10条动态

# 优先级队列
ZADD priority_queue 1 "low_priority_task"
ZADD priority_queue 5 "high_priority_task"
ZADD priority_queue 3 "medium_priority_task"
ZRANGE priority_queue 0 0            # 获取最高优先级任务
```

## 🔧 高级命令

### 事务命令
```bash
# 事务控制
MULTI                                # 开始事务
EXEC                                 # 执行事务
DISCARD                              # 取消事务
WATCH key [key ...]                  # 监视键
UNWATCH                              # 取消监视
```

### 发布订阅命令
```bash
# 发布订阅
SUBSCRIBE channel [channel ...]      # 订阅频道
UNSUBSCRIBE [channel ...]            # 取消订阅
PUBLISH channel message              # 发布消息
PSUBSCRIBE pattern [pattern ...]     # 模式订阅
PUNSUBSCRIBE [pattern ...]           # 取消模式订阅
PUBSUB subcommand [argument [argument ...]]  # 查看发布订阅信息
```

### 脚本命令
```bash
# Lua脚本
EVAL script numkeys key [key ...] arg [arg ...]
EVALSHA sha1 numkeys key [key ...] arg [arg ...]
SCRIPT LOAD script                   # 加载脚本
SCRIPT EXISTS sha1 [sha1 ...]        # 检查脚本是否存在
SCRIPT FLUSH                         # 清空脚本缓存
SCRIPT KILL                          # 杀死正在执行的脚本
```

### 连接命令
```bash
# 客户端连接
AUTH password                        # 认证
QUIT                                 # 退出连接
SELECT index                         # 选择数据库
ECHO message                         # 回显消息
PING [message]                       # PING测试
```

## 🎯 命令组合应用

### 场景1：用户签到系统
```bash
# 使用Bitmap实现签到
# 一年最多365天，可以用一个key存储
SETBIT sign:2024:user:1001 0 1       # 1月1日签到
SETBIT sign:2024:user:1001 1 1       # 1月2日签到
BITCOUNT sign:2024:user:1001         # 统计总签到天数
GETBIT sign:2024:user:1001 0         # 检查某天是否签到
```

### 场景2：限流系统
```bash
# 滑动窗口限流
# key格式：rate_limit:{user_id}:{time_window}
INCR rate_limit:user:1001:1640995200
EXPIRE rate_limit:user:1001:1640995200 60  # 1分钟过期

# 检查是否超过限制
GET rate_limit:user:1001:1640995200
```

### 场景3：分布式锁
```bash
# 简单分布式锁
SET lock:resource "locked" NX EX 30  # 30秒过期
# 业务逻辑处理...
DEL lock:resource

# 带值验证的安全锁
SET lock:resource "uuid_value" NX EX 30
# 释放时验证拥有者
EVAL "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end" 1 lock:resource uuid_value
```

### 场景4：缓存预热
```bash
# 批量加载热点数据
MGET user:1001 user:1002 user:1003 user:1004 user:1005
# 或者使用Pipeline提高性能
```

## 📊 性能监控命令

### 信息查看
```bash
# 基础信息
INFO
INFO server
INFO clients
INFO memory
INFO persistence
INFO stats
INFO replication
INFO cpu
INFO commandstats
INFO cluster
INFO keyspace
```

### 性能分析
```bash
# 慢查询日志
SLOWLOG GET [count]
SLOWLOG LEN
SLOWLOG RESET

# 命令耗时统计
CONFIG SET latency-monitor-threshold 100  # 设置阈值100毫秒
LATENCY LATEST
LATENCY HISTORY command
LATENCY GRAPH command
```

### 内存分析
```bash
# 内存使用情况
MEMORY USAGE key
MEMORY STATS
MEMORY DOCTOR

# 大键扫描
REDISCLI MONITOR | grep -E "(hgetall|smembers|lrange|zrange)" | head -20
```

## 🔍 调试和故障排查

### 常用调试命令
```bash
# 监控命令执行
MONITOR                              # 实时监控所有命令

# 客户端连接诊断
CLIENT LIST
CLIENT INFO

# 键空间分析
SCAN 0 MATCH * COUNT 1000
```

### 常见问题排查
```bash
# 检查连接数
INFO clients

# 检查内存使用
INFO memory

# 检查持久化状态
INFO persistence

# 检查慢查询
SLOWLOG GET 10
```

## 🛠️ 实用脚本示例

### 数据备份脚本
```bash
#!/bin/bash
# Redis数据备份脚本

REDIS_CLI="/usr/local/bin/redis-cli"
BACKUP_DIR="/backup/redis"
DATE=$(date +%Y%m%d_%H%M%S)

# 执行BGSAVE
$REDIS_CLI BGSAVE

# 等待备份完成
while [ $($REDIS_CLI LASTSAVE) -lt $(date +%s) ]; do
    sleep 1
done

# 复制dump文件
cp /var/lib/redis/dump.rdb $BACKUP_DIR/dump_$DATE.rdb

# 清理7天前的备份
find $BACKUP_DIR -name "dump_*.rdb" -mtime +7 -delete

echo "Backup completed: dump_$DATE.rdb"
```

### 性能测试脚本
```bash
#!/bin/bash
# Redis性能测试脚本

HOST="localhost"
PORT="6379"

echo "=== Redis Performance Test ==="

# SET性能测试
echo "Testing SET performance..."
redis-benchmark -h $HOST -p $PORT -t set -n 100000 -q

# GET性能测试
echo "Testing GET performance..."
redis-benchmark -h $HOST -p $PORT -t get -n 100000 -q

# Pipeline性能测试
echo "Testing Pipeline performance..."
redis-benchmark -h $HOST -p $PORT -t set,get -n 100000 -P 10 -q

# 连接数测试
echo "Testing concurrent connections..."
redis-benchmark -h $HOST -p $PORT -t set,get -n 100000 -c 50 -q
```

### 监控告警脚本
```bash
#!/bin/bash
# Redis监控告警脚本

REDIS_CLI="redis-cli"
THRESHOLD_MEMORY=80  # 内存使用率阈值%
THRESHOLD_CONNECTIONS=1000  # 连接数阈值

# 检查内存使用率
memory_used=$($REDIS_CLI INFO memory | grep "used_memory_rss_human" | cut -d: -f2)
memory_max=$($REDIS_CLI INFO memory | grep "maxmemory_human" | cut -d: -f2)

# 检查连接数
connected_clients=$($REDIS_CLI INFO clients | grep "connected_clients" | cut -d: -f2)

# 发送告警（示例）
if [ "$memory_used" -gt "$THRESHOLD_MEMORY" ]; then
    echo "WARNING: Redis memory usage is high: $memory_used%"
fi

if [ "$connected_clients" -gt "$THRESHOLD_CONNECTIONS" ]; then
    echo "WARNING: Redis connections count is high: $connected_clients"
fi
```

## 🎯 本章小结

通过本章学习，你应该掌握了：
- ✅ Redis各类命令的详细使用方法
- ✅ 命令组合应用的实际场景
- ✅ 性能监控和调试技巧
- ✅ 实用的运维脚本编写
- ✅ 故障排查的基本方法

## 📚 下一步学习

下一章我们将深入学习Redis的持久化机制：
- RDB持久化原理和配置
- AOF持久化机制详解
- 持久化策略选择和优化
- 数据恢复和备份方案

---
*Redis学习教程 - 第3章完*