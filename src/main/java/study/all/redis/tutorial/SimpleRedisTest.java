package study.all.redis.tutorial;

import redis.clients.jedis.Jedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis简单测试程序
 * 验证Redis环境和基本连接
 */
public class SimpleRedisTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRedisTest.class);
    
    public static void main(String[] args) {
        logger.info("=========================================");
        logger.info("      Redis环境测试程序启动");
        logger.info("=========================================\n");
        
        try {
            // 测试基本连接
            testBasicConnection();
            
            // 测试String操作
            testStringOperations();
            
            // 测试Hash操作
            testHashOperations();
            
            logger.info("\n🎉 所有测试通过！Redis环境配置成功！");
            
        } catch (Exception e) {
            logger.error("测试过程中出现错误", e);
        }
    }
    
    /**
     * 测试基本连接
     */
    private static void testBasicConnection() {
        logger.info("=== 测试基本连接 ===");
        
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String pong = jedis.ping();
            logger.info("PING响应: {}", pong);
            
            String serverInfo = jedis.info("server");
            logger.info("服务器信息获取成功");
            
        } catch (Exception e) {
            logger.error("基本连接测试失败", e);
            throw new RuntimeException("无法连接到Redis服务器", e);
        }
        
        logger.info("✅ 基本连接测试通过\n");
    }
    
    /**
     * 测试String类型操作
     */
    private static void testStringOperations() {
        logger.info("=== 测试String操作 ===");
        
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            
            // 基本SET/GET
            jedis.set("test:name", "Redis Tutorial");
            String name = jedis.get("test:name");
            logger.info("SET/GET测试: {}", name);
            
            // 数值操作
            jedis.set("test:counter", "0");
            jedis.incr("test:counter");
            jedis.incrBy("test:counter", 5);
            String counter = jedis.get("test:counter");
            logger.info("计数器测试: {}", counter);
            
            // 过期时间
            jedis.setex("test:temp", 10, "temporary data");
            long ttl = jedis.ttl("test:temp");
            logger.info("过期时间测试: {}秒", ttl);
            
            // 批量操作
            jedis.mset("test:key1", "value1", "test:key2", "value2");
            java.util.List<String> values = jedis.mget("test:key1", "test:key2");
            logger.info("批量操作测试: {}", values);
            
        } catch (Exception e) {
            logger.error("String操作测试失败", e);
        }
        
        logger.info("✅ String操作测试通过\n");
    }
    
    /**
     * 测试Hash类型操作
     */
    private static void testHashOperations() {
        logger.info("=== 测试Hash操作 ===");
        
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            
            String hashKey = "test:user:1001";
            
            // 设置Hash字段
            java.util.Map<String, String> userData = new java.util.HashMap<>();
            userData.put("name", "张三");
            userData.put("age", "25");
            userData.put("email", "zhangsan@example.com");
            jedis.hmset(hashKey, userData);
            
            // 获取字段
            String name = jedis.hget(hashKey, "name");
            String age = jedis.hget(hashKey, "age");
            logger.info("用户信息 - 姓名: {}, 年龄: {}", name, age);
            
            // 数值操作
            jedis.hincrBy(hashKey, "age", 1);
            String newAge = jedis.hget(hashKey, "age");
            logger.info("年龄递增后: {}", newAge);
            
            // 获取所有字段
            java.util.Map<String, String> allData = jedis.hgetAll(hashKey);
            logger.info("完整用户数据: {}", allData);
            
        } catch (Exception e) {
            logger.error("Hash操作测试失败", e);
        }
        
        logger.info("✅ Hash操作测试通过\n");
    }
}