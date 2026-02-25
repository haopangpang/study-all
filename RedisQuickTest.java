import redis.clients.jedis.Jedis;

public class RedisQuickTest {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("      Redis快速测试程序");
        System.out.println("=========================================\n");
        
        try {
            // 测试基本连接
            testBasicConnection();
            
            // 测试String操作
            testStringOperations();
            
            // 测试Hash操作
            testHashOperations();
            
            System.out.println("\n🎉 所有测试通过！Redis环境配置成功！");
            
        } catch (Exception e) {
            System.err.println("测试过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicConnection() {
        System.out.println("=== 测试基本连接 ===");
        
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String pong = jedis.ping();
            System.out.println("PING响应: " + pong);
            
            String serverInfo = jedis.info("server");
            System.out.println("服务器信息获取成功");
            
        } catch (Exception e) {
            System.err.println("基本连接测试失败: " + e.getMessage());
            throw new RuntimeException("无法连接到Redis服务器", e);
        }
        
        System.out.println("✅ 基本连接测试通过\n");
    }
    
    private static void testStringOperations() {
        System.out.println("=== 测试String操作 ===");
        
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            
            // 基本SET/GET
            jedis.set("test:name", "Redis Tutorial");
            String name = jedis.get("test:name");
            System.out.println("SET/GET测试: " + name);
            
            // 数值操作
            jedis.set("test:counter", "0");
            jedis.incr("test:counter");
            jedis.incrBy("test:counter", 5);
            String counter = jedis.get("test:counter");
            System.out.println("计数器测试: " + counter);
            
            // 过期时间
            jedis.setex("test:temp", 10, "temporary data");
            long ttl = jedis.ttl("test:temp");
            System.out.println("过期时间测试: " + ttl + "秒");
            
            // 批量操作
            jedis.mset("test:key1", "value1", "test:key2", "value2");
            java.util.List<String> values = jedis.mget("test:key1", "test:key2");
            System.out.println("批量操作测试: " + values);
            
        } catch (Exception e) {
            System.err.println("String操作测试失败: " + e.getMessage());
        }
        
        System.out.println("✅ String操作测试通过\n");
    }
    
    private static void testHashOperations() {
        System.out.println("=== 测试Hash操作 ===");
        
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
            System.out.println("用户信息 - 姓名: " + name + ", 年龄: " + age);
            
            // 数值操作
            jedis.hincrBy(hashKey, "age", 1);
            String newAge = jedis.hget(hashKey, "age");
            System.out.println("年龄递增后: " + newAge);
            
            // 获取所有字段
            java.util.Map<String, String> allData = jedis.hgetAll(hashKey);
            System.out.println("完整用户数据: " + allData);
            
        } catch (Exception e) {
            System.err.println("Hash操作测试失败: " + e.getMessage());
        }
        
        System.out.println("✅ Hash操作测试通过\n");
    }
}