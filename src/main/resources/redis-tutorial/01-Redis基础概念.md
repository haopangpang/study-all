# Redis学习教程 - 第1章：Redis基础概念

## 🎯 本章目标
- 理解Redis是什么及其核心特性
- 掌握Redis的应用场景
- 了解Redis与其他存储系统的区别
- 学会Redis的基本架构

## 🔍 什么是Redis？

### Redis定义
Redis (Remote Dictionary Server) 是一个开源的、基于内存的键值存储系统，它支持多种数据结构，并提供高性能的数据访问能力。

### 核心特征
- **内存存储**：数据主要存储在内存中，访问速度极快
- **持久化**：支持数据持久化到磁盘
- **丰富数据类型**：支持字符串、哈希、列表、集合等多种数据结构
- **原子操作**：所有操作都是原子性的
- **高可用**：支持主从复制、哨兵模式、集群部署

## 🏗️ Redis架构概述

### 单机架构
```
┌─────────────────┐
│   Client应用    │
└─────────┬───────┘
          │ TCP连接
          ▼
┌─────────────────┐
│   Redis Server  │
├─────────────────┤
│   内存数据库    │
│   持久化引擎    │
│   网络处理模块  │
└─────────────────┘
```

### 主从架构
```
┌─────────────┐
│  Master节点 │
└──────┬──────┘
       │ 同步数据
       ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Slave节点1  │    │ Slave节点2  │    │ Slave节点3  │
└─────────────┘    └─────────────┘    └─────────────┘
```

## 💡 Redis核心优势

### 1. 极致性能
- **内存操作**：纯内存读写，性能极高
- **单线程模型**：避免上下文切换开销
- **非阻塞IO**：使用多路复用技术处理并发

### 2. 丰富的数据结构
| 数据类型 | 应用场景 | 典型命令 |
|---------|---------|---------|
| String | 缓存、计数器 | SET, GET, INCR |
| Hash | 对象存储 | HSET, HGET, HMGET |
| List | 消息队列 | LPUSH, RPUSH, LPOP |
| Set | 去重、标签 | SADD, SMEMBERS, SINTER |
| Sorted Set | 排行榜 | ZADD, ZRANGE, ZSCORE |

### 3. 原子性保证
所有Redis操作都是原子性的，无需担心并发问题：
```bash
# 原子性递增操作
INCR counter  # 等价于 GET + SET + INCR 的原子操作
```

## 🎯 应用场景分析

### 1. 缓存系统
```java
// 传统数据库查询缓存
String userId = "12345";
String userInfo = redis.get("user:" + userId);
if (userInfo == null) {
    userInfo = database.getUserById(userId);
    redis.setex("user:" + userId, 3600, userInfo); // 缓存1小时
}
```

### 2. 会话存储
```
Session Key: session:user:12345
Value: {"userId":"12345","loginTime":1640995200,"permissions":["read","write"]}
```

### 3. 分布式锁
```java
// 使用Redis实现分布式锁
boolean lockAcquired = redis.setnx("lock:resource", "locked");
if (lockAcquired) {
    redis.expire("lock:resource", 30); // 30秒过期
    // 执行关键业务逻辑
    redis.del("lock:resource");
}
```

### 4. 消息队列
```java
// 生产者
redis.lpush("task_queue", "task_data");

// 消费者
String task = redis.brpop(0, "task_queue"); // 阻塞弹出
processTask(task);
```

### 5. 实时排行榜
```java
// 更新用户积分
redis.zadd("leaderboard", score, userId);

// 获取Top10
List<String> topUsers = redis.zrevrange("leaderboard", 0, 9);
```

## ⚖️ Redis vs 其他存储系统

### Redis vs Memcached
| 特性 | Redis | Memcached |
|------|-------|-----------|
| 数据持久化 | 支持 | 不支持 |
| 数据类型 | 丰富 | 仅字符串 |
| 原子操作 | 支持 | 有限支持 |
| 集群支持 | 原生支持 | 需要客户端分片 |

### Redis vs 关系型数据库
| 特性 | Redis | MySQL/PostgreSQL |
|------|-------|------------------|
| 存储介质 | 内存 | 磁盘 |
| 查询性能 | 极高 | 中等 |
| 持久化 | 可选 | 强制 |
| 复杂查询 | 有限支持 | 强大 |
| 事务支持 | 简单事务 | ACID事务 |

### Redis vs MongoDB
| 特性 | Redis | MongoDB |
|------|-------|---------|
| 数据模型 | 键值对 | 文档 |
| 性能 | 更高 | 中等 |
| 持久化 | RDB/AOF | WiredTiger |
| 查询能力 | 简单 | 复杂查询 |

## 🛠️ Redis安装与配置

### Windows安装
```powershell
# 使用Chocolatey安装
choco install redis-64

# 或者下载Redis Windows版本
# https://github.com/microsoftarchive/redis/releases
```

### Linux安装
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server

# CentOS/RHEL
sudo yum install epel-release
sudo yum install redis

# 启动服务
sudo systemctl start redis
sudo systemctl enable redis
```

### Docker安装
```bash
# 拉取镜像
docker pull redis:latest

# 运行容器
docker run -d -p 6379:6379 --name my-redis redis:latest

# 带持久化的运行
docker run -d -p 6379:6379 -v /mydata/redis/data:/data --name my-redis redis:latest redis-server --appendonly yes
```

### 基础配置
```conf
# redis.conf 基础配置示例
bind 127.0.0.1                    # 绑定IP地址
port 6379                         # 监听端口
daemonize yes                     # 后台运行
pidfile /var/run/redis.pid        # PID文件路径
loglevel notice                   # 日志级别
logfile /var/log/redis.log        # 日志文件
databases 16                      # 数据库数量
save 900 1                        # 900秒内至少1个key变化则保存
save 300 10                       # 300秒内至少10个key变化则保存
save 60 10000                     # 60秒内至少10000个key变化则保存
dbfilename dump.rdb               # RDB文件名
dir /var/lib/redis                # 工作目录
appendonly yes                    # 开启AOF持久化
appendfilename "appendonly.aof"   # AOF文件名
```

## 🧪 基础操作演示

### 命令行交互
```bash
# 连接Redis
redis-cli

# 基础操作
127.0.0.1:6379> SET name "Redis教程"
OK
127.0.0.1:6379> GET name
"Redis教程"
127.0.0.1:6379> EXISTS name
(integer) 1
127.0.0.1:6379> DEL name
(integer) 1
```

### Java客户端连接
```java
import redis.clients.jedis.Jedis;

public class RedisConnectionDemo {
    public static void main(String[] args) {
        // 连接到本地Redis
        Jedis jedis = new Jedis("localhost", 6379);
        
        // 测试连接
        System.out.println("服务器正在运行: " + jedis.ping());
        
        // 基本操作
        jedis.set("tutorial", "Redis学习教程");
        String value = jedis.get("tutorial");
        System.out.println("获取的值: " + value);
        
        // 关闭连接
        jedis.close();
    }
}
```

## 📊 性能基准测试

### Redis-benchmark工具
```bash
# 测试SET性能
redis-benchmark -t set -n 100000 -q

# 测试GET性能  
redis-benchmark -t get -n 100000 -q

# 综合性能测试
redis-benchmark -n 100000 -c 50 -q
```

### 典型性能指标
- **SET操作**：约100,000+ ops/sec
- **GET操作**：约100,000+ ops/sec
- **内存占用**：每个key-value约占用50-100字节
- **响应时间**：通常在亚毫秒级别

## 🔒 安全考虑

### 基础安全配置
```conf
# 设置密码认证
requirepass your_strong_password

# 禁止危险命令
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command KEYS ""
rename-command CONFIG ""

# 限制访问IP
bind 127.0.0.1 192.168.1.100
```

### SSL/TLS加密
```conf
# 启用TLS
tls-port 6380
tls-cert-file /path/to/server.crt
tls-key-file /path/to/server.key
tls-ca-cert-file /path/to/ca.crt
```

## 🎯 本章小结

通过本章学习，你应该掌握了：
- ✅ Redis的基本概念和核心特性
- ✅ Redis的主要应用场景
- ✅ Redis与其他存储系统的区别
- ✅ Redis的安装配置方法
- ✅ 基础的操作演示

## 📚 下一步学习

下一章我们将深入学习Redis的各种数据类型及其使用方法，包括：
- String类型的高级用法
- Hash类型在对象存储中的应用
- List类型实现消息队列
- Set和Sorted Set的实际应用场景

---
*Redis学习教程 - 第1章完*