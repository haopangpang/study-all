package study.all.redisson.demo;

import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.all.redisson.config.RedissonConfig;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 基础功能演示类
 * 展示 RMap、RList、RSet 等核心数据结构的使用
 */
public class BasicOperationsDemo {
    private static final Logger logger = LoggerFactory.getLogger(BasicOperationsDemo.class);
    private final RedissonClient client;
    
    public BasicOperationsDemo() {
        this.client = RedissonConfig.getClient();
    }
    
    /**
     * 演示 RMap（分布式 Map）的使用
     */
    public void demonstrateRMap() {
        logger.info("=== RMap 演示开始 ===");
        
        try {
            // 获取分布式 Map
            RMap<String, String> map = client.getMap("demo:users");
            
            // 基本操作
            map.put("user1", "张三");
            map.put("user2", "李四");
            map.put("user3", "王五");
            
            logger.info("Map 大小: {}", map.size());
            logger.info("user1 的值: {}", map.get("user1"));
            logger.info("是否存在 user2: {}", map.containsKey("user2"));
            
            // 获取所有键值对
            map.readAllEntrySet().forEach(entry -> 
                logger.info("键: {}, 值: {}", entry.getKey(), entry.getValue())
            );
            
            // 删除操作
            map.remove("user3");
            logger.info("删除 user3 后的大小: {}", map.size());
            
            // 清空 Map
            map.clear();
            logger.info("清空后的大小: {}", map.size());
            
        } catch (Exception e) {
            logger.error("RMap 演示出错", e);
        }
        
        logger.info("=== RMap 演示结束 ===\n");
    }
    
    /**
     * 演示 RList（分布式 List）的使用
     */
    public void demonstrateRList() {
        logger.info("=== RList 演示开始 ===");
        
        try {
            // 获取分布式 List
            RList<String> list = client.getList("demo:messages");
            
            // 添加元素
            list.add("第一条消息");
            list.add("第二条消息");
            list.add("第三条消息");
            
            logger.info("List 大小: {}", list.size());
            logger.info("第一个元素: {}", list.get(0));
            logger.info("最后一个元素: {}", list.get(list.size() - 1));
            
            // 遍历列表
            for (int i = 0; i < list.size(); i++) {
                logger.info("索引 {}: {}", i, list.get(i));
            }
            
            // 插入操作
            list.add(1, "插入的消息");
            logger.info("插入后的第二个元素: {}", list.get(1));
            
            // 删除操作
            String removed = list.remove(0);
            logger.info("删除的元素: {}", removed);
            
            // 清空列表
            list.clear();
            logger.info("清空后的大小: {}", list.size());
            
        } catch (Exception e) {
            logger.error("RList 演示出错", e);
        }
        
        logger.info("=== RList 演示结束 ===\n");
    }
    
    /**
     * 演示 RSet（分布式 Set）的使用
     */
    public void demonstrateRSet() {
        logger.info("=== RSet 演示开始 ===");
        
        try {
            // 获取分布式 Set
            RSet<String> set = client.getSet("demo:tags");
            
            // 添加元素
            set.add("Java");
            set.add("Redis");
            set.add("Spring");
            set.add("Java"); // 重复元素不会被添加
            
            logger.info("Set 大小: {}", set.size());
            logger.info("是否包含 Java: {}", set.contains("Java"));
            logger.info("是否包含 Python: {}", set.contains("Python"));
            
            // 遍历集合
            set.forEach(tag -> logger.info("标签: {}", tag));
            
            // 集合运算
            RSet<String> anotherSet = client.getSet("demo:frameworks");
            anotherSet.add("Spring");
            anotherSet.add("MyBatis");
            anotherSet.add("Hibernate");
            
            // 交集
            RSet<String> intersection = client.getSet("demo:intersection_result");
            intersection.addAll(set.readAll());
            intersection.retainAll(anotherSet.readAll());
            logger.info("交集: {}", intersection.readAll());
            
            // 并集
            RSet<String> union = client.getSet("demo:union_result");
            union.addAll(set.readAll());
            union.addAll(anotherSet.readAll());
            logger.info("并集: {}", union.readAll());
            
            // 差集
            RSet<String> diff = client.getSet("demo:diff_result");
            diff.addAll(set.readAll());
            diff.removeAll(anotherSet.readAll());
            logger.info("差集: {}", diff.readAll());
            
            // 清空集合
            set.clear();
            anotherSet.clear();
            
        } catch (Exception e) {
            logger.error("RSet 演示出错", e);
        }
        
        logger.info("=== RSet 演示结束 ===\n");
    }
    
    /**
     * 演示原子长整型操作
     */
    public void demonstrateAtomicLong() {
        logger.info("=== AtomicLong 演示开始 ===");
        
        try {
            RAtomicLong counter = client.getAtomicLong("demo:counter");
            
            // 初始化为 0
            counter.set(0);
            
            // 原子递增
            long value1 = counter.incrementAndGet();
            logger.info("递增后值: {}", value1);
            
            // 原子递减
            long value2 = counter.decrementAndGet();
            logger.info("递减后值: {}", value2);
            
            // 原子加法
            long value3 = counter.addAndGet(10);
            logger.info("加10后值: {}", value3);
            
            // 比较并设置
            boolean success = counter.compareAndSet(10, 100);
            logger.info("CAS操作结果: {}, 当前值: {}", success, counter.get());
            
            // 删除计数器
            counter.delete();
            
        } catch (Exception e) {
            logger.error("AtomicLong 演示出错", e);
        }
        
        logger.info("=== AtomicLong 演示结束 ===\n");
    }
    
    /**
     * 演示带过期时间的数据结构
     */
    public void demonstrateExpiringData() {
        logger.info("=== 带过期时间的数据结构演示开始 ===");
        
        try {
            // 带过期时间的 Map
            RMap<String, String> expiringMap = client.getMap("demo:expiring_map");
            
            // 设置键值对，5秒后过期
            expiringMap.put("temp_key", "临时数据");
            client.getBucket("demo:expiring_map:temp_key").expire(5, TimeUnit.SECONDS);
            logger.info("设置临时数据: temp_key = 临时数据");
            
            // 立即获取
            logger.info("立即获取: {}", expiringMap.get("temp_key"));
            
            // 等待6秒后再次获取
            Thread.sleep(6000);
            logger.info("6秒后获取: {}", expiringMap.get("temp_key"));
            
            // 清空
            expiringMap.clear();
            
        } catch (Exception e) {
            logger.error("带过期时间的数据结构演示出错", e);
        }
        
        logger.info("=== 带过期时间的数据结构演示结束 ===\n");
    }
    
    /**
     * 运行所有基础演示
     */
    public void runAllDemos() {
        logger.info("开始 Redisson 基础功能演示...\n");
        
        demonstrateRMap();
        demonstrateRList();
        demonstrateRSet();
        demonstrateAtomicLong();
        demonstrateExpiringData();
        
        logger.info("Redisson 基础功能演示完成！");
    }
}