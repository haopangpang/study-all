# Redis快速入门指南

## 🚀 5分钟快速上手

### 1. 安装Redis
```bash
# Windows (使用Chocolatey)
choco install redis-64

# Linux Ubuntu
sudo apt update && sudo apt install redis-server

# Docker方式（推荐）
docker run -d -p 6379:6379 --name my-redis redis:latest
```

### 2. 启动Redis服务
```bash
# Linux
sudo systemctl start redis

# Windows
redis-server

# Docker
docker start my-redis
```

### 3. 连接Redis
```bash
# 使用命令行客户端
redis-cli

# 测试连接
127.0.0.1:6379> PING
PONG
```

### 4. 基础操作示例
```bash
# 字符串操作
SET name "Redis新手"
GET name
INCR counter

# 列表操作
LPUSH todos "学习Redis"
LPUSH todos "练习命令"
LRANGE todos 0 -1

# 哈希操作
HMSET user:1001 name "张三" age 25
HGET user:1001 name
```

## 📚 核心概念速记

### 5种数据类型
| 类型 | 用途 | 典型场景 |
|------|------|----------|
| **String** | 简单键值 | 缓存、计数器 |
| **Hash** | 对象存储 | 用户信息、配置 |
| **List** | 有序列表 | 消息队列、时间线 |
| **Set** | 无序集合 | 标签、去重 |
| **Sorted Set** | 有序集合 | 排行榜、优先级 |

### 常用命令分类
```bash
# 通用命令
KEYS *          # 查看所有键
EXISTS key      # 检查键是否存在
DEL key         # 删除键
EXPIRE key 60   # 设置60秒过期

# String命令
SET/GET         # 设置/获取值
INCR/DECR       # 自增/自减
APPEND          # 追加字符串

# Hash命令  
HSET/HGET       # 设置/获取字段
HMSET/HMGET     # 批量操作
HINCRBY         # 字段数值增加

# List命令
LPUSH/RPUSH     # 左/右插入
LPOP/RPOP       # 左/右弹出
LRANGE          # 获取范围元素

# Set命令
SADD/SMEMBERS   # 添加/获取元素
SISMEMBER       # 检查成员
SINTER/SUNION   # 集合运算

# Sorted Set命令
ZADD/ZRANGE     # 添加/获取元素
ZINCRBY         # 增加分数
ZREVRANGE       # 倒序获取
```

## 💡 实用技巧

### 1. 批量操作提升性能
```bash
# 使用Pipeline
redis-cli --pipe < commands.txt

# Java代码示例
Pipeline pipeline = jedis.pipelined();
for(int i=0; i<1000; i++) {
    pipeline.set("key:"+i, "value:"+i);
}
pipeline.sync();
```

### 2. 过期时间管理
```bash
# 设置过期时间
SETEX session_token 3600 "user_session_data"
PEXPIRE temporary_data 5000  # 毫秒

# 查看过期时间
TTL key
PTTL key
```

### 3. 原子性操作
```bash
# 分布式锁
SET lock:resource "locked" NX EX 30

# 计数器
INCR page_views
INCRBY user_score 10
```

## 🔧 Java快速集成

### Maven依赖
```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>4.3.1</version>
</dependency>
```

### 基础使用示例
```java
import redis.clients.jedis.Jedis;

public class RedisQuickStart {
    public static void main(String[] args) {
        // 连接Redis
        Jedis jedis = new Jedis("localhost", 6379);
        
        // 字符串操作
        jedis.set("hello", "world");
        String value = jedis.get("hello");
        System.out.println(value); // 输出: world
        
        // 数值操作
        jedis.incr("counter");
        long count = jedis.incrBy("counter", 5);
        System.out.println(count); // 输出: 6
        
        // 关闭连接
        jedis.close();
    }
}
```

## 🎯 常见应用场景

### 1. 缓存系统
```java
String cacheKey = "user:profile:" + userId;
String userInfo = jedis.get(cacheKey);

if (userInfo == null) {
    userInfo = loadUserInfoFromDB(userId);
    jedis.setex(cacheKey, 3600, userInfo); // 缓存1小时
}
```

### 2. 会话存储
```java
// 存储用户会话
String sessionId = UUID.randomUUID().toString();
Map<String, String> sessionData = new HashMap<>();
sessionData.put("userId", "12345");
sessionData.put("loginTime", String.valueOf(System.currentTimeMillis()));
jedis.hmset("session:" + sessionId, sessionData);
jedis.expire("session:" + sessionId, 1800); // 30分钟过期
```

### 3. 消息队列
```java
// 生产者
jedis.lpush("task_queue", taskData);

// 消费者
List<String> result = jedis.brpop(0, "task_queue");
String task = result.get(1);
processTask(task);
```

### 4. 排行榜
```java
// 更新积分
jedis.zincrby("leaderboard", scoreIncrement, userId);

// 获取Top10
Set<String> topUsers = jedis.zrevrange("leaderboard", 0, 9);
```

## ⚡ 性能优化要点

### 1. 连接池配置
```java
JedisPoolConfig config = new JedisPoolConfig();
config.setMaxTotal(20);
config.setMaxIdle(10);
config.setMinIdle(5);
JedisPool pool = new JedisPool(config, "localhost", 6379);
```

### 2. 数据结构选择
```
缓存数据 → String (JSON格式)
用户对象 → Hash
消息队列 → List
去重统计 → Set
排行榜   → Sorted Set
```

### 3. 内存优化
```bash
# 查看内存使用
INFO memory

# 删除过期键
EXPIRE key 3600

# 使用较小的键名
# 推荐: u:1001:p  而不是 user:1001:profile
```

## 🛡️ 安全配置

### 基础安全设置
```conf
# redis.conf
requirepass your_strong_password
rename-command FLUSHDB ""
rename-command FLUSHALL ""
bind 127.0.0.1
port 6379
```

### Java连接认证
```java
Jedis jedis = new Jedis("localhost", 6379);
jedis.auth("your_strong_password");
```

## 📊 监控和调试

### 基础监控命令
```bash
# 服务器信息
INFO

# 性能统计
INFO stats

# 客户端连接
CLIENT LIST

# 慢查询日志
SLOWLOG GET 10
```

### 性能测试
```bash
# 基准测试
redis-benchmark -t set,get -n 100000 -q

# 管道测试
redis-benchmark -t set -n 100000 -P 10 -q
```

## 🔍 故障排查

### 常见问题解决

**1. 连接拒绝**
```bash
# 检查Redis服务状态
sudo systemctl status redis

# 检查端口监听
netstat -tlnp | grep 6379
```

**2. 内存不足**
```bash
# 查看内存使用
INFO memory

# 配置最大内存
# redis.conf: maxmemory 2gb
# maxmemory-policy allkeys-lru
```

**3. 性能下降**
```bash
# 查看慢查询
SLOWLOG GET 20

# 监控命令执行
MONITOR
```

## 📚 学习资源推荐

### 官方资源
- 官方文档: https://redis.io/documentation
- 命令手册: https://redis.io/commands
- GitHub: https://github.com/redis/redis

### 中文资源
- Redis中文网: http://www.redis.cn/
- 《Redis设计与实现》在线阅读
- 相关技术博客和教程

### 实践建议
1. **动手实验** - 在本地搭建环境多练习
2. **项目应用** - 找实际项目场景使用
3. **性能测试** - 关注不同场景下的表现
4. **源码阅读** - 深入理解实现原理

---

*Redis快速入门指南 - 助你快速掌握Redis核心技能*