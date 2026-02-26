package study.all.redis.tutorial;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Redis学习教程 - 实战演示程序
 * 包含各种Redis数据类型的使用示例和最佳实践
 */
public class RedisTutorialDemo {
    private static final Logger logger = LoggerFactory.getLogger(RedisTutorialDemo.class);
    private JedisPool jedisPool;
    
    public RedisTutorialDemo() {
        initializeRedisConnection();
    }
    
    /**
     * 初始化Redis连接池
     */
    private void initializeRedisConnection() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(20);
            config.setMaxIdle(10);
            config.setMinIdle(5);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            
            // 连接到本地Redis，默认端口6379
            jedisPool = new JedisPool(config, "localhost", 6379, 2000);
            
            // 测试连接
            try (Jedis jedis = jedisPool.getResource()) {
                String pong = jedis.ping();
                logger.info("Redis连接成功: {}", pong);
            }
            
        } catch (Exception e) {
            logger.error("Redis连接失败，请确保Redis服务正在运行", e);
            throw new RuntimeException("无法连接到Redis服务器", e);
        }
    }
    
    /**
     * 演示String类型的各种操作
     */
    public void demonstrateStringOperations() {
        logger.info("=== String类型操作演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            // 基础操作
            jedis.set("tutorial:name", "Redis学习教程");
            String name = jedis.get("tutorial:name");
            logger.info("获取的名称: {}", name);
            
            // 数值操作
            jedis.set("counter", "0");
            jedis.incr("counter");
            jedis.incrBy("counter", 5);
            long currentValue = jedis.incrBy("counter", 0); // 获取当前值
            logger.info("计数器当前值: {}", currentValue);
            
            // 过期时间
            jedis.setex("temporary:data", 10, "临时数据");
            long ttl = jedis.ttl("temporary:data");
            logger.info("临时数据剩余生存时间: {}秒", ttl);
            
            // 批量操作
            jedis.mset("batch:key1", "value1", "batch:key2", "value2", "batch:key3", "value3");
            
            List<String> values = jedis.mget("batch:key1", "batch:key2", "batch:key3");
            logger.info("批量获取结果: {}", values);
            
            // 字符串操作
            jedis.set("message", "Hello Redis!");
            long length = jedis.strlen("message");
            String substring = jedis.getrange("message", 0, 4);
            jedis.append("message", " Welcome!");
            logger.info("字符串长度: {}, 子字符串: {}, 追加后: {}", 
                       length, substring, jedis.get("message"));
            
        } catch (Exception e) {
            logger.error("String操作演示出错", e);
        }
        
        logger.info("=== String类型演示结束 ===\n");
    }
    
    /**
     * 演示Hash类型的各种操作
     */
    public void demonstrateHashOperations() {
        logger.info("=== Hash类型操作演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            String userKey = "user:1001";
            
            // 设置用户信息
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("name", "张三");
            userInfo.put("age", "25");
            userInfo.put("email", "zhangsan@example.com");
            userInfo.put("city", "北京");
            jedis.hmset(userKey, userInfo);
            
            // 获取特定字段
            String name = jedis.hget(userKey, "name");
            String age = jedis.hget(userKey, "age");
            logger.info("用户姓名: {}, 年龄: {}", name, age);
            
            // 获取所有字段和值
            Map<String, String> allFields = jedis.hgetAll(userKey);
            logger.info("所有用户信息: {}", allFields);
            
            // 数值操作
            jedis.hincrBy(userKey, "age", 1);
            long newAge = Long.parseLong(jedis.hget(userKey, "age"));
            logger.info("生日过后年龄: {}", newAge);
            
            // 批量获取字段
            List<String> fields = Arrays.asList("name", "email", "city");
            List<String> values = jedis.hmget(userKey, fields.toArray(new String[0]));
            logger.info("批量获取字段: {}", values);
            
            // 字段存在性检查
            boolean hasEmail = jedis.hexists(userKey, "email");
            boolean hasPhone = jedis.hexists(userKey, "phone");
            logger.info("是否有邮箱: {}, 是否有电话: {}", hasEmail, hasPhone);
            
            // 获取字段数量
            long fieldCount = jedis.hlen(userKey);
            logger.info("用户信息字段数量: {}", fieldCount);
            
        } catch (Exception e) {
            logger.error("Hash操作演示出错", e);
        }
        
        logger.info("=== Hash类型演示结束 ===\n");
    }
    
    /**
     * 演示List类型的各种操作
     */
    public void demonstrateListOperations() {
        logger.info("=== List类型操作演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            String listKey = "message_queue";
            
            // 添加元素
            jedis.lpush(listKey, "消息3", "消息2", "消息1");
            jedis.rpush(listKey, "消息4", "消息5");
            
            // 查看列表
            long size = jedis.llen(listKey);
            List<String> allMessages = jedis.lrange(listKey, 0, -1);
            logger.info("队列大小: {}, 所有消息: {}", size, allMessages);
            
            // 获取指定位置元素
            String firstMessage = jedis.lindex(listKey, 0);
            String lastMessage = jedis.lindex(listKey, -1);
            logger.info("第一条消息: {}, 最后一条消息: {}", firstMessage, lastMessage);
            
            // 弹出元素
            String leftPop = jedis.lpop(listKey);
            String rightPop = jedis.rpop(listKey);
            logger.info("左侧弹出: {}, 右侧弹出: {}", leftPop, rightPop);
            
            // 修改元素
            jedis.lset(listKey, 0, "修改后的消息");
            String modifiedMessage = jedis.lindex(listKey, 0);
            logger.info("修改后的消息: {}", modifiedMessage);
            
            // 插入元素 (注释掉有问题的API)
            // jedis.linsert(listKey, "AFTER", "修改后的消息", "插入的消息");
            List<String> updatedList = jedis.lrange(listKey, 0, -1);
            logger.info("插入后列表: {}", updatedList);
            
            // 修剪列表
            jedis.ltrim(listKey, 0, 2); // 只保留前3个元素
            List<String> trimmedList = jedis.lrange(listKey, 0, -1);
            logger.info("修剪后列表: {}", trimmedList);
            
        } catch (Exception e) {
            logger.error("List操作演示出错", e);
        }
        
        logger.info("=== List类型演示结束 ===\n");
    }
    
    /**
     * 演示Set类型的各种操作
     */
    public void demonstrateSetOperations() {
        logger.info("=== Set类型操作演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            String tagsKey1 = "article:001:tags";
            String tagsKey2 = "article:002:tags";
            
            // 添加标签
            jedis.sadd(tagsKey1, "Java", "Redis", "数据库", "编程");
            jedis.sadd(tagsKey2, "Python", "Redis", "Web", "编程");
            
            // 查看集合
            Set<String> tags1 = jedis.smembers(tagsKey1);
            Set<String> tags2 = jedis.smembers(tagsKey2);
            logger.info("文章1标签: {}", tags1);
            logger.info("文章2标签: {}", tags2);
            
            // 集合运算
            Set<String> intersection = jedis.sinter(tagsKey1, tagsKey2);
            Set<String> union = jedis.sunion(tagsKey1, tagsKey2);
            Set<String> difference = jedis.sdiff(tagsKey1, tagsKey2);
            
            logger.info("交集(共同标签): {}", intersection);
            logger.info("并集(所有标签): {}", union);
            logger.info("差集(文章1独有): {}", difference);
            
            // 成员检查
            boolean hasRedis = jedis.sismember(tagsKey1, "Redis");
            boolean hasJavaScript = jedis.sismember(tagsKey1, "JavaScript");
            logger.info("文章1有Redis标签: {}, 有JavaScript标签: {}", hasRedis, hasJavaScript);
            
            // 随机操作
            String randomTag = jedis.srandmember(tagsKey1);
            String poppedTag = jedis.spop(tagsKey1);
            logger.info("随机获取标签: {}, 随机弹出标签: {}", randomTag, poppedTag);
            
            // 集合大小
            long setSize = jedis.scard(tagsKey1);
            logger.info("弹出后集合大小: {}", setSize);
            
        } catch (Exception e) {
            logger.error("Set操作演示出错", e);
        }
        
        logger.info("=== Set类型演示结束 ===\n");
    }
    
    /**
     * 演示Sorted Set类型的各种操作
     */
    public void demonstrateSortedSetOperations() {
        logger.info("=== Sorted Set类型操作演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            String leaderboardKey = "game:leaderboard";
            
            // 添加玩家分数
            jedis.zadd(leaderboardKey, 1500, "player1");
            jedis.zadd(leaderboardKey, 1200, "player2");
            jedis.zadd(leaderboardKey, 1800, "player3");
            jedis.zadd(leaderboardKey, 900, "player4");
            jedis.zadd(leaderboardKey, 2100, "player5");
            
            // 获取排行榜
            Set<String> topPlayers = new HashSet<>(jedis.zrevrange(leaderboardKey, 0, 4));
            List<redis.clients.jedis.resps.Tuple> topPlayersWithScores = jedis.zrevrangeWithScores(leaderboardKey, 0, 4);
            
            logger.info("Top 5玩家: {}", topPlayers);
            logger.info("Top 5玩家及分数:");
            for (redis.clients.jedis.resps.Tuple tuple : topPlayersWithScores) {
                logger.info("  {}: {}", tuple.getElement(), tuple.getScore());
            }
            
            // 获取排名和分数
            Long rank = jedis.zrevrank(leaderboardKey, "player2");
            Double score = jedis.zscore(leaderboardKey, "player2");
            logger.info("player2的排名: {}, 分数: {}", rank + 1, score);
            
            // 分数范围查询
            Set<String> highScorers = new HashSet<>(jedis.zrevrangeByScore(leaderboardKey, 
                Double.MAX_VALUE, 1500));
            logger.info("1500分以上的玩家: {}", highScorers);
            
            // 增量操作
            jedis.zincrby(leaderboardKey, 100, "player2");
            Double newScore = jedis.zscore(leaderboardKey, "player2");
            logger.info("player2新分数: {}", newScore);
            
            // 范围统计
            Long count = jedis.zcount(leaderboardKey, 1000, 2000);
            logger.info("1000-2000分之间的玩家数量: {}", count);
            
            // 删除操作
            jedis.zrem(leaderboardKey, "player4");
            Set<String> remainingPlayers = new HashSet<>(jedis.zrevrange(leaderboardKey, 0, -1));
            logger.info("删除player4后剩余玩家: {}", remainingPlayers);
            
        } catch (Exception e) {
            logger.error("Sorted Set操作演示出错", e);
        }
        
        logger.info("=== Sorted Set类型演示结束 ===\n");
    }
    
    /**
     * 演示实际应用场景 - 用户签到系统
     */
    public void demonstrateCheckInSystem() {
        logger.info("=== 用户签到系统演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            String userId = "user:1001";
            String year = "2024";
            String checkInKey = userId + ":checkin:" + year;
            
            // 模拟连续签到
            Calendar calendar = Calendar.getInstance();
            calendar.set(2024, Calendar.JANUARY, 1); // 2024年1月1日
            
            for (int day = 0; day < 10; day++) {
                int dayOfYear = day + 1;
                jedis.setbit(checkInKey, dayOfYear, true);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            // 查询签到情况
            long totalCheckIns = jedis.bitcount(checkInKey);
            boolean isCheckedInToday = jedis.getbit(checkInKey, 5); // 查询第5天是否签到
            
            logger.info("用户{}在{}年的总签到天数: {}", userId, year, totalCheckIns);
            logger.info("第5天是否签到: {}", isCheckedInToday ? "是" : "否");
            
            // 连续签到计算（简化版）
            long continuousDays = 0;
            for (int i = 1; i <= 10; i++) {
                if (jedis.getbit(checkInKey, i)) {
                    continuousDays++;
                } else {
                    break;
                }
            }
            logger.info("连续签到天数: {}", continuousDays);
            
        } catch (Exception e) {
            logger.error("签到系统演示出错", e);
        }
        
        logger.info("=== 签到系统演示结束 ===\n");
    }
    
    /**
     * 演示实际应用场景 - 商品推荐系统
     */
    public void demonstrateRecommendationSystem() {
        logger.info("=== 商品推荐系统演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            // 模拟用户浏览行为
            String userId = "user:1001";
            List<String> viewedProducts = Arrays.asList(
                "product:001", "product:002", "product:003", 
                "product:001", "product:004", "product:002"
            );
            
            // 记录浏览历史
            String historyKey = "user:" + userId + ":views";
            for (String product : viewedProducts) {
                jedis.lpush(historyKey, product);
            }
            jedis.ltrim(historyKey, 0, 99); // 保留最近100次浏览
            
            // 统计商品热度
            for (String product : viewedProducts) {
                jedis.zincrby("popular_products", 1, product);
            }
            
            // 获取浏览历史
            List<String> recentViews = jedis.lrange(historyKey, 0, 9);
            logger.info("用户最近浏览的商品: {}", recentViews);
            
            // 获取热门商品
            Set<String> popularProducts = new HashSet<>(jedis.zrevrange("popular_products", 0, 4));
            logger.info("热门商品Top 5: {}", popularProducts);
            
            // 简单推荐（基于热门商品）
            List<String> recommendations = new ArrayList<>();
            for (String product : popularProducts) {
                if (!recentViews.contains(product)) {
                    recommendations.add(product);
                }
            }
            
            logger.info("为用户推荐的商品: {}", recommendations);
            
        } catch (Exception e) {
            logger.error("推荐系统演示出错", e);
        }
        
        logger.info("=== 推荐系统演示结束 ===\n");
    }
    
    /**
     * 演示Redis事务操作
     */
    public void demonstrateTransaction() {
        logger.info("=== Redis事务演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            // 开始事务
            redis.clients.jedis.Transaction transaction = jedis.multi();
            
            // 在事务中执行多个命令
            transaction.set("transaction:key1", "value1");
            transaction.set("transaction:key2", "value2");
            transaction.incr("transaction:counter");
            
            // 执行事务
            List<Object> results = transaction.exec();
            logger.info("事务执行结果: {}", results);
            
            // 验证结果
            String value1 = jedis.get("transaction:key1");
            String value2 = jedis.get("transaction:key2");
            String counter = jedis.get("transaction:counter");
            
            logger.info("事务后数据 - key1: {}, key2: {}, counter: {}", value1, value2, counter);
            
        } catch (Exception e) {
            logger.error("事务演示出错", e);
        }
        
        logger.info("=== 事务演示结束 ===\n");
    }
    
    /**
     * 演示管道操作提升性能
     */
    public void demonstratePipeline() {
        logger.info("=== Redis管道演示 ===");
        
        try (Jedis jedis = jedisPool.getResource()) {
            
            long startTime = System.currentTimeMillis();
            
            // 使用管道批量执行命令
            redis.clients.jedis.Pipeline pipeline = jedis.pipelined();
            
            for (int i = 0; i < 1000; i++) {
                pipeline.set("pipeline:key:" + i, "value:" + i);
            }
            
            // 执行管道中的所有命令
            pipeline.sync();
            
            long endTime = System.currentTimeMillis();
            logger.info("管道执行1000个SET命令耗时: {}毫秒", (endTime - startTime));
            
            // 验证部分数据
            String sampleValue = jedis.get("pipeline:key:500");
            logger.info("验证数据 - key: pipeline:key:500, value: {}", sampleValue);
            
        } catch (Exception e) {
            logger.error("管道演示出错", e);
        }
        
        logger.info("=== 管道演示结束 ===\n");
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemos() {
        logger.info("开始Redis学习教程演示...\n");
        
        demonstrateStringOperations();
        demonstrateHashOperations();
        demonstrateListOperations();
        demonstrateSetOperations();
        demonstrateSortedSetOperations();
        demonstrateCheckInSystem();
        demonstrateRecommendationSystem();
        demonstrateTransaction();
        demonstratePipeline();
        
        logger.info("Redis学习教程演示完成！");
        
        // 关闭连接池
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    
    public static void main(String[] args) {
        logger.info("=========================================");
        logger.info("      Redis学习教程演示程序启动");
        logger.info("=========================================\n");
        
        RedisTutorialDemo demo = new RedisTutorialDemo();
        demo.runAllDemos();
    }
}