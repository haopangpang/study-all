package study.all.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redisson 客户端配置类
 * 提供单例模式的 Redisson 客户端实例
 */
public class RedissonConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedissonConfig.class);
    
    private static volatile RedissonClient redissonClient;
    private static final Object lock = new Object();
    
    // 默认 Redis 连接配置
    private static final String DEFAULT_REDIS_ADDRESS = "redis://127.0.0.1:6379";
    private static final int DEFAULT_CONNECTION_POOL_SIZE = 64;
    private static final int DEFAULT_TIMEOUT = 3000;
    
    /**
     * 获取 Redisson 客户端实例（单例模式）
     * @return RedissonClient 实例
     */
    public static RedissonClient getClient() {
        if (redissonClient == null) {
            synchronized (lock) {
                if (redissonClient == null) {
                    redissonClient = createClient();
                }
            }
        }
        return redissonClient;
    }
    
    /**
     * 创建 Redisson 客户端实例
     * @return RedissonClient 实例
     */
    private static RedissonClient createClient() {
        try {
            Config config = new Config();
            
            // 配置单节点模式
            config.useSingleServer()
                  .setAddress(DEFAULT_REDIS_ADDRESS)
                  .setConnectionPoolSize(DEFAULT_CONNECTION_POOL_SIZE)
                  .setTimeout(DEFAULT_TIMEOUT)
                  .setRetryAttempts(3)
                  .setRetryInterval(1500);
            
            // 设置序列化方式
            config.setCodec(new org.redisson.client.codec.StringCodec());
            
            RedissonClient client = Redisson.create(config);
            logger.info("Redisson 客户端初始化成功，连接地址: {}", DEFAULT_REDIS_ADDRESS);
            return client;
            
        } catch (Exception e) {
            logger.error("Redisson 客户端初始化失败", e);
            throw new RuntimeException("Failed to initialize Redisson client", e);
        }
    }
    
    /**
     * 关闭 Redisson 客户端连接
     */
    public static void shutdown() {
        if (redissonClient != null) {
            try {
                redissonClient.shutdown();
                logger.info("Redisson 客户端已关闭");
            } catch (Exception e) {
                logger.error("关闭 Redisson 客户端时发生错误", e);
            }
            redissonClient = null;
        }
    }
    
    /**
     * 检查客户端是否可用
     * @return boolean 是否可用
     */
    public static boolean isAvailable() {
        try {
            return redissonClient != null && !redissonClient.isShutdown();
        } catch (Exception e) {
            logger.warn("检查 Redisson 客户端状态时发生异常", e);
            return false;
        }
    }
}