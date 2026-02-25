# Redis学习教程 - 第2章：Redis数据类型详解

## 🎯 本章目标
- 深入掌握Redis 5种核心数据类型
- 理解各种数据类型的底层实现原理
- 学会在实际场景中合理选择数据类型
- 掌握各数据类型的性能特点和最佳实践

## 🔤 String（字符串）

### 基础特性
- 最基本的数据类型
- 可以存储字符串、整数、浮点数
- 最大存储512MB数据
- 支持原子性操作

### 常用命令
```bash
# 基础操作
SET key value                    # 设置键值
GET key                          # 获取值
EXISTS key                       # 判断键是否存在
DEL key                          # 删除键

# 数值操作
INCR key                         # 自增1
DECR key                         # 自减1
INCRBY key increment             # 增加指定数值
DECRBY key decrement             # 减少指定数值

# 字符串操作
APPEND key value                 # 追加字符串
STRLEN key                       # 获取字符串长度
GETRANGE key start end           # 获取子字符串
SETRANGE key offset value        # 设置指定位置的字符

# 过期时间
SETEX key seconds value          # 设置值并指定过期时间
PEXPIRE key milliseconds         # 设置毫秒级过期时间
TTL key                          # 查看剩余生存时间
```

### 实际应用场景

#### 1. 缓存系统
```java
// 用户信息缓存
jedis.setex("user:1001", 3600, "{\"id\":1001,\"name\":\"张三\",\"email\":\"zhangsan@example.com\"}");

// 商品详情缓存
String productInfo = jedis.get("product:2001");
if (productInfo == null) {
    productInfo = loadFromDatabase(productId);
    jedis.setex("product:" + productId, 1800, productInfo);
}
```

#### 2. 计数器
```java
// 页面访问统计
jedis.incr("page_views:/index.html");

// 用户积分系统
jedis.incrBy("user_score:1001", 10);  // 增加10分
long currentScore = jedis.incrBy("user_score:1001", 0);  // 获取当前分数

// 限流计数器
String key = "rate_limit:" + userId + ":" + System.currentTimeMillis() / 60000;
if (jedis.incr(key) == 1) {
    jedis.expire(key, 60);  // 1分钟过期
}
```

#### 3. 分布式锁
```java
// 简单分布式锁实现
public boolean acquireLock(String lockKey, String lockValue, int expireSeconds) {
    String result = jedis.set(lockKey, lockValue, "NX", "EX", expireSeconds);
    return "OK".equals(result);
}

// 释放锁（需要验证拥有者）
public boolean releaseLock(String lockKey, String lockValue) {
    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                   "return redis.call('del', KEYS[1]) else return 0 end";
    Object result = jedis.eval(script, 1, lockKey, lockValue);
    return "1".equals(result.toString());
}
```

### 性能优化技巧
```bash
# 批量操作提升性能
MGET key1 key2 key3              # 一次获取多个值
MSET key1 val1 key2 val2         # 一次设置多个值

# Pipeline批量执行
pipeline.set("key1", "value1");
pipeline.set("key2", "value2");
pipeline.incr("counter");
pipeline.sync();  # 一次性发送所有命令
```

## 📦 Hash（哈希）

### 基础特性
- 类似于Java的HashMap
- 适合存储对象
- 支持字段级别的操作
- 内存效率高

### 常用命令
```bash
# 基础操作
HSET key field value             # 设置字段值
HGET key field                   # 获取字段值
HMSET key field1 val1 field2 val2 # 设置多个字段
HMGET key field1 field2          # 获取多个字段
HGETALL key                      # 获取所有字段和值
HEXISTS key field                # 判断字段是否存在
HDEL key field1 field2           # 删除字段

# 数值操作
HINCRBY key field increment      # 字段值增加
HINCRBYFLOAT key field increment # 浮点数增加

# 信息查询
HLEN key                         # 字段数量
HKEYS key                        # 所有字段名
HVALS key                        # 所有字段值
```

### 实际应用场景

#### 1. 对象存储
```java
// 存储用户对象
Map<String, String> user = new HashMap<>();
user.put("name", "李四");
user.put("age", "25");
user.put("email", "lisi@example.com");
user.put("city", "北京");
jedis.hmset("user:1002", user);

// 获取特定字段
List<String> fields = Arrays.asList("name", "email");
List<String> values = jedis.hmget("user:1002", fields.toArray(new String[0]));

// 更新单个属性
jedis.hset("user:1002", "age", "26");
```

#### 2. 购物车实现
```java
// 添加商品到购物车
jedis.hincrBy("cart:user:1001", "product:2001", 1);
jedis.hincrBy("cart:user:1001", "product:2002", 2);

// 获取购物车内容
Map<String, String> cart = jedis.hgetAll("cart:user:1001");
for (Map.Entry<String, String> entry : cart.entrySet()) {
    System.out.println("商品: " + entry.getKey() + ", 数量: " + entry.getValue());
}

// 修改商品数量
jedis.hset("cart:user:1001", "product:2001", "3");

// 删除商品
jedis.hdel("cart:user:1001", "product:2002");
```

#### 3. 配置管理
```java
// 系统配置存储
Map<String, String> config = new HashMap<>();
config.put("max_connections", "100");
config.put("timeout", "30");
config.put("debug_mode", "false");
jedis.hmset("system:config", config);

// 动态修改配置
jedis.hset("system:config", "max_connections", "200");
String maxConn = jedis.hget("system:config", "max_connections");
```

## 📋 List（列表）

### 基础特性
- 双向链表实现
- 支持两端插入和删除
- 可用作栈、队列、阻塞队列
- 支持阻塞操作

### 常用命令
```bash
# 添加元素
LPUSH key value                  # 左侧插入
RPUSH key value                  # 右侧插入
LPUSHX key value                 # 仅当列表存在时左侧插入
RPUSHX key value                 # 仅当列表存在时右侧插入

# 移除元素
LPOP key                         # 左侧弹出
RPOP key                         # 右侧弹出
BLPOP key timeout                # 阻塞左侧弹出
BRPOP key timeout                # 阻塞右侧弹出

# 查看元素
LRANGE key start stop            # 获取范围内的元素
LINDEX key index                 # 获取指定位置元素
LLEN key                         # 列表长度

# 修改元素
LSET key index value             # 设置指定位置的值
LINSERT key BEFORE|AFTER pivot value  # 插入元素

# 删除元素
LREM key count value             # 删除指定值的元素
LTRIM key start end              # 保留指定范围的元素
```

### 实际应用场景

#### 1. 消息队列
```java
// 生产者
public void produceMessage(String queueName, String message) {
    jedis.lpush(queueName, message);
    System.out.println("生产消息: " + message);
}

// 消费者
public String consumeMessage(String queueName) {
    String message = jedis.brpop(0, queueName).get(1);  // 阻塞等待
    System.out.println("消费消息: " + message);
    return message;
}

// 使用示例
produceMessage("task_queue", "处理订单12345");
String task = consumeMessage("task_queue");
processOrder(task);
```

#### 2. 最新消息列表
```java
// 存储用户最新动态
public void addLatestActivity(String userId, String activity) {
    String key = "user:" + userId + ":activities";
    jedis.lpush(key, activity);
    // 只保留最近100条记录
    jedis.ltrim(key, 0, 99);
}

// 获取用户最新动态
public List<String> getUserActivities(String userId, int count) {
    String key = "user:" + userId + ":activities";
    return jedis.lrange(key, 0, count - 1);
}
```

#### 3. 任务调度
```java
// 延迟任务队列
public void scheduleTask(String task, long delaySeconds) {
    long executeTime = System.currentTimeMillis() + delaySeconds * 1000;
    String scheduledTask = executeTime + ":" + task;
    jedis.zadd("delayed_tasks", executeTime, scheduledTask);
}

// 任务处理器
public void processScheduledTasks() {
    long now = System.currentTimeMillis();
    Set<String> readyTasks = jedis.zrangeByScore("delayed_tasks", 0, now);
    
    for (String task : readyTasks) {
        if (jedis.zrem("delayed_tasks", task) > 0) {
            String actualTask = task.substring(task.indexOf(":") + 1);
            executeTask(actualTask);
        }
    }
}
```

## 🔘 Set（集合）

### 基础特性
- 无序且不重复的元素集合
- 基于哈希表实现
- 支持集合运算（交集、并集、差集）
- O(1)时间复杂度的查找

### 常用命令
```bash
# 基础操作
SADD key member                  # 添加元素
SMEMBERS key                     # 获取所有元素
SISMEMBER key member             # 判断元素是否存在
SCARD key                        # 获取集合大小
SREM key member                  # 删除元素
SPOP key                         # 随机弹出元素
SRANDMEMBER key [count]          # 随机获取元素

# 集合运算
SINTER key1 key2                 # 交集
SUNION key1 key2                 # 并集
SDIFF key1 key2                  # 差集
SINTERSTORE dest key1 key2       # 交集并存储
SUNIONSTORE dest key1 key2       # 并集并存储
SDIFFSTORE dest key1 key2        # 差集并存储
```

### 实际应用场景

#### 1. 标签系统
```java
// 为文章添加标签
public void addArticleTags(String articleId, Set<String> tags) {
    String key = "article:" + articleId + ":tags";
    for (String tag : tags) {
        jedis.sadd(key, tag);
    }
}

// 查找具有相同标签的文章
public Set<String> findArticlesByTag(String tag) {
    Set<String> articles = new HashSet<>();
    ScanParams params = new ScanParams().match("article:*:tags");
    ScanResult<String> result = jedis.scan("0", params);
    
    for (String key : result.getResult()) {
        if (jedis.sismember(key, tag)) {
            String articleId = key.split(":")[1];
            articles.add(articleId);
        }
    }
    return articles;
}
```

#### 2. 好友关系
```java
// 添加好友
public void addFriend(String userId, String friendId) {
    jedis.sadd("friends:" + userId, friendId);
    jedis.sadd("friends:" + friendId, userId);
}

// 获取共同好友
public Set<String> getCommonFriends(String user1, String user2) {
    return jedis.sinter("friends:" + user1, "friends:" + user2);
}

// 推荐可能认识的人
public Set<String> suggestFriends(String userId) {
    Set<String> friends = jedis.smembers("friends:" + userId);
    Set<String> suggestions = new HashSet<>();
    
    for (String friend : friends) {
        Set<String> friendFriends = jedis.smembers("friends:" + friend);
        friendFriends.removeAll(friends);
        friendFriends.remove(userId);  // 移除自己
        suggestions.addAll(friendFriends);
    }
    
    return suggestions;
}
```

#### 3. 去重统计
```java
// 统计独立访客
public void recordVisitor(String pageUrl, String visitorId) {
    String key = "visitors:" + pageUrl + ":" + getDate();
    jedis.sadd(key, visitorId);
}

public long getUniqueVisitors(String pageUrl, String date) {
    String key = "visitors:" + pageUrl + ":" + date;
    return jedis.scard(key);
}
```

## 📊 Sorted Set（有序集合）

### 基础特性
- 元素带有分数(score)的有序集合
- 分数可以重复，但成员唯一
- 支持按分数范围查询
- 支持排名查询

### 常用命令
```bash
# 基础操作
ZADD key score member            # 添加元素
ZSCORE key member                # 获取元素分数
ZCARD key                        # 获取集合大小
ZCOUNT key min max               # 统计分数范围内元素数

# 排名查询
ZRANK key member                 # 获取元素排名(从小到大)
ZREVRANK key member              # 获取元素排名(从大到小)
ZRANGE key start stop            # 按排名获取元素
ZREVRANGE key start stop         # 按排名倒序获取元素

# 分数查询
ZRANGEBYSCORE key min max        # 按分数范围获取元素
ZREVRANGEBYSCORE key max min     # 按分数范围倒序获取
ZREMRANGEBYRANK key start stop   # 按排名删除元素
ZREMRANGEBYSCORE key min max     # 按分数范围删除元素

# 增量操作
ZINCRBY key increment member     # 增加元素分数
```

### 实际应用场景

#### 1. 排行榜系统
```java
// 更新用户积分
public void updateUserScore(String userId, double scoreIncrement) {
    jedis.zincrby("leaderboard", scoreIncrement, userId);
}

// 获取排行榜Top N
public List<Tuple> getTopPlayers(int topN) {
    return jedis.zrevrangeWithScores("leaderboard", 0, topN - 1);
}

// 获取用户的排名
public Long getUserRank(String userId) {
    return jedis.zrevrank("leaderboard", userId);
}

// 获取附近排名的用户
public List<Tuple> getNearbyPlayers(String userId, int range) {
    Long rank = jedis.zrevrank("leaderboard", userId);
    if (rank == null) return new ArrayList<>();
    
    long start = Math.max(0, rank - range);
    long end = rank + range;
    return jedis.zrevrangeWithScores("leaderboard", start, end);
}
```

#### 2. 时间轴排序
```java
// 发布动态
public void postStatus(String userId, String content) {
    long timestamp = System.currentTimeMillis();
    String statusKey = "status:" + userId + ":" + timestamp;
    jedis.set(statusKey, content);
    
    // 添加到用户时间线
    jedis.zadd("timeline:" + userId, timestamp, statusKey);
    jedis.zremrangeByRank("timeline:" + userId, 0, -1001);  // 保留最近1000条
    
    // 添加到粉丝时间线
    Set<String> followers = jedis.smembers("followers:" + userId);
    for (String follower : followers) {
        jedis.zadd("timeline:" + follower, timestamp, statusKey);
        jedis.zremrangeByRank("timeline:" + follower, 0, -1001);
    }
}

// 获取时间线
public List<String> getTimeline(String userId, int page, int size) {
    long start = page * size;
    long end = start + size - 1;
    
    Set<String> statusKeys = jedis.zrevrange("timeline:" + userId, start, end);
    List<String> statuses = new ArrayList<>();
    
    for (String key : statusKeys) {
        statuses.add(jedis.get(key));
    }
    
    return statuses;
}
```

#### 3. 优先级队列
```java
// 添加任务到优先级队列
public void addPriorityTask(String task, double priority) {
    jedis.zadd("priority_queue", priority, task);
}

// 获取最高优先级任务
public String getHighestPriorityTask() {
    Set<String> tasks = jedis.zrange("priority_queue", 0, 0);
    if (tasks.isEmpty()) return null;
    
    String task = tasks.iterator().next();
    jedis.zrem("priority_queue", task);
    return task;
}

// 批量获取任务
public List<String> getPriorityTasks(int count) {
    Set<String> tasks = jedis.zrange("priority_queue", 0, count - 1);
    jedis.zrem("priority_queue", tasks.toArray(new String[0]));
    return new ArrayList<>(tasks);
}
```

## 🔄 数据类型选择指南

### 选择决策树
```
需要存储什么数据？
├── 简单的键值对？
│   ├── 需要计数？→ String (INCR/DECR)
│   └── 简单字符串？→ String
├── 结构化对象？
│   ├── 需要部分更新？→ Hash
│   └── 整体替换？→ String (JSON)
├── 有序列表？
│   ├── 需要两端操作？→ List
│   └── 需要按权重排序？→ Sorted Set
├── 去重集合？
│   ├── 需要排序？→ Sorted Set
│   └── 无需排序？→ Set
└── 需要集合运算？→ Set
```

### 性能对比表
| 数据类型 | 内存效率 | 查找性能 | 插入性能 | 适用场景 |
|---------|---------|---------|---------|---------|
| String | 高 | O(1) | O(1) | 缓存、计数器 |
| Hash | 中 | O(1) | O(1) | 对象存储 |
| List | 中 | O(N) | O(1) | 队列、栈 |
| Set | 中 | O(1) | O(1) | 去重、集合运算 |
| Sorted Set | 低 | O(logN) | O(logN) | 排序、排行榜 |

### 内存优化建议
```java
// 1. 合理使用Hash替代多个String
// 不推荐：user:1001:name, user:1001:age, user:1001:email
// 推荐：user:1001 {name:"张三", age:"25", email:"..."}

// 2. 使用整数对象共享
// Redis会对0-9999的整数进行对象共享

// 3. 合理设置过期时间
jedis.expire("temporary_data", 3600);  // 1小时后过期

// 4. 压缩长字符串
String compressedData = compress(largeString);
jedis.set("large_data", compressedData);
```

## 🧪 实战练习

### 练习1：实现简单的微博系统
```java
public class WeiboSystem {
    private Jedis jedis = new Jedis("localhost");
    
    // 发布微博
    public void postWeibo(String userId, String content) {
        long timestamp = System.currentTimeMillis();
        String postId = "post:" + userId + ":" + timestamp;
        jedis.hset(postId, "content", content);
        jedis.hset(postId, "timestamp", String.valueOf(timestamp));
        jedis.hset(postId, "userId", userId);
        
        // 添加到用户时间线
        jedis.zadd("timeline:" + userId, timestamp, postId);
        
        // 添加到关注者时间线
        Set<String> followers = jedis.smembers("followers:" + userId);
        for (String follower : followers) {
            jedis.zadd("timeline:" + follower, timestamp, postId);
        }
    }
    
    // 关注用户
    public void followUser(String followerId, String followeeId) {
        jedis.sadd("following:" + followerId, followeeId);
        jedis.sadd("followers:" + followeeId, followerId);
        
        // 将被关注者的微博添加到关注者时间线
        Set<Tuple> posts = jedis.zrangeWithScores("timeline:" + followeeId, 0, -1);
        for (Tuple post : posts) {
            jedis.zadd("timeline:" + followerId, post.getScore(), post.getElement());
        }
    }
    
    // 获取时间线
    public List<Map<String, String>> getTimeline(String userId, int page, int size) {
        long start = page * size;
        long end = start + size - 1;
        
        Set<String> postIds = jedis.zrevrange("timeline:" + userId, start, end);
        List<Map<String, String>> timeline = new ArrayList<>();
        
        for (String postId : postIds) {
            Map<String, String> post = jedis.hgetAll(postId);
            timeline.add(post);
        }
        
        return timeline;
    }
}
```

### 练习2：实现商品推荐系统
```java
public class RecommendationSystem {
    private Jedis jedis = new Jedis("localhost");
    
    // 记录用户浏览行为
    public void recordView(String userId, String productId) {
        // 记录浏览历史
        jedis.lpush("views:" + userId, productId);
        jedis.ltrim("views:" + userId, 0, 99);  // 保留最近100次浏览
        
        // 增加商品热度
        jedis.zincrby("popular_products", 1, productId);
        
        // 记录用户偏好
        Set<String> categories = getProductCategories(productId);
        for (String category : categories) {
            jedis.zincrby("user_preference:" + userId, 1, category);
        }
    }
    
    // 获取热门商品
    public List<String> getPopularProducts(int limit) {
        Set<String> products = jedis.zrevrange("popular_products", 0, limit - 1);
        return new ArrayList<>(products);
    }
    
    // 个性化推荐
    public List<String> recommendProducts(String userId, int limit) {
        // 基于用户偏好的推荐
        Set<String> preferences = jedis.zrevrange("user_preference:" + userId, 0, 9);
        
        List<String> recommendations = new ArrayList<>();
        for (String category : preferences) {
            Set<String> categoryProducts = getCategoryProducts(category);
            recommendations.addAll(categoryProducts);
            
            if (recommendations.size() >= limit * 2) break;
        }
        
        // 去重并限制数量
        return recommendations.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
```

## 🎯 本章小结

通过本章学习，你应该掌握了：
- ✅ 5种Redis数据类型的特性和使用场景
- ✅ 各数据类型的常用命令和操作方法
- ✅ 实际应用中的最佳实践
- ✅ 数据类型选择的决策依据
- ✅ 内存优化和性能调优技巧

## 📚 下一步学习

下一章我们将学习Redis的高级功能：
- 事务处理机制
- Lua脚本编程
- 发布订阅模式
- 管道和批量操作
- 监控和调试工具

---
*Redis学习教程 - 第2章完*