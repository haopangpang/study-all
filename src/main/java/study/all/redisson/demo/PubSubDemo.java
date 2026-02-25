package study.all.redisson.demo;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.all.redisson.config.RedissonConfig;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 发布订阅演示类
 * 展示消息发布和订阅的功能
 */
public class PubSubDemo {
    private static final Logger logger = LoggerFactory.getLogger(PubSubDemo.class);
    private final RedissonClient client;
    
    public PubSubDemo() {
        this.client = RedissonConfig.getClient();
    }
    
    /**
     * 简单字符串消息演示
     */
    public void demonstrateStringMessage() {
        logger.info("=== 字符串消息演示开始 ===");
        
        String topicName = "demo:string_topic";
        RTopic topic = client.getTopic(topicName);
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // 订阅消息
        int listenerId = topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                logger.info("收到消息: {} 来自频道: {}", msg, channel);
                latch.countDown();
            }
        });
        
        logger.info("已订阅频道: {}", topicName);
        
        // 发布消息
        topic.publish("Hello Redisson!");
        topic.publish("这是一条测试消息");
        topic.publish("消息传递成功");
        
        try {
            // 等待所有消息接收完成
            if (latch.await(5, TimeUnit.SECONDS)) {
                logger.info("所有消息接收完成");
            } else {
                logger.warn("部分消息未及时接收");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待消息时被中断", e);
        }
        
        // 取消订阅
        topic.removeListener(listenerId);
        logger.info("取消订阅");
        
        logger.info("=== 字符串消息演示结束 ===\n");
    }
    
    /**
     * 自定义对象消息演示
     */
    public void demonstrateObjectMessage() {
        logger.info("=== 对象消息演示开始 ===");
        
        String topicName = "demo:object_topic";
        RTopic topic = client.getTopic(topicName);
        
        CountDownLatch latch = new CountDownLatch(2);
        
        // 订阅用户对象消息
        int listenerId = topic.addListener(UserMessage.class, new MessageListener<UserMessage>() {
            @Override
            public void onMessage(CharSequence channel, UserMessage userMsg) {
                logger.info("收到用户消息 - ID: {}, 姓名: {}, 操作: {}", 
                           userMsg.getId(), userMsg.getName(), userMsg.getAction());
                latch.countDown();
            }
        });
        
        logger.info("已订阅用户消息频道: {}", topicName);
        
        // 发布用户对象消息
        UserMessage user1 = new UserMessage(1L, "张三", "登录");
        UserMessage user2 = new UserMessage(2L, "李四", "注册");
        
        topic.publish(user1);
        topic.publish(user2);
        
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待对象消息时被中断", e);
        }
        
        topic.removeListener(listenerId);
        logger.info("取消用户消息订阅");
        
        logger.info("=== 对象消息演示结束 ===\n");
    }
    
    /**
     * 多频道订阅演示
     */
    public void demonstrateMultipleTopics() {
        logger.info("=== 多频道订阅演示开始 ===");
        
        // 创建多个频道
        RTopic newsTopic = client.getTopic("demo:news");
        RTopic sportsTopic = client.getTopic("demo:sports");
        RTopic techTopic = client.getTopic("demo:technology");
        
        CountDownLatch latch = new CountDownLatch(4);
        
        // 统一消息监听器
        MessageListener<String> universalListener = new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                logger.info("[{}] 收到消息: {}", channel, msg);
                latch.countDown();
            }
        };
        
        // 订阅所有频道
        int newsListenerId = newsTopic.addListener(String.class, universalListener);
        int sportsListenerId = sportsTopic.addListener(String.class, universalListener);
        int techListenerId = techTopic.addListener(String.class, universalListener);
        
        logger.info("已订阅所有频道");
        
        // 发布不同类型的消息
        newsTopic.publish("今日头条新闻");
        sportsTopic.publish("体育赛事更新");
        techTopic.publish("科技前沿动态");
        newsTopic.publish("最新社会新闻");
        
        try {
            if (latch.await(5, TimeUnit.SECONDS)) {
                logger.info("所有频道消息接收完成");
            } else {
                logger.warn("部分频道消息未及时接收");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待多频道消息时被中断", e);
        }
        
        // 取消所有订阅
        newsTopic.removeListener(newsListenerId);
        sportsTopic.removeListener(sportsListenerId);
        techTopic.removeListener(techListenerId);
        
        logger.info("取消所有频道订阅");
        logger.info("=== 多频道订阅演示结束 ===\n");
    }
    
    /**
     * 模式匹配订阅演示
     */
    public void demonstratePatternSubscription() {
        logger.info("=== 模式匹配订阅演示开始 ===");
        
        // 注意：Redisson 的模式订阅需要使用特殊方法
        // 这里演示概念，实际使用时可能需要不同的API
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // 订阅以 "demo:user:" 开头的所有频道
        RTopic userTopic1 = client.getTopic("demo:user:login");
        RTopic userTopic2 = client.getTopic("demo:user:logout");
        RTopic userTopic3 = client.getTopic("demo:user:update");
        
        MessageListener<String> userListener = new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                logger.info("用户活动 - 频道: {}, 消息: {}", channel, msg);
                latch.countDown();
            }
        };
        
        int listenerId1 = userTopic1.addListener(String.class, userListener);
        int listenerId2 = userTopic2.addListener(String.class, userListener);
        int listenerId3 = userTopic3.addListener(String.class, userListener);
        
        logger.info("已订阅用户活动相关频道");
        
        // 发布用户活动消息
        userTopic1.publish("用户张三登录系统");
        userTopic2.publish("用户李四退出登录");
        userTopic3.publish("用户王五更新个人信息");
        
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待模式匹配消息时被中断", e);
        }
        
        // 清理订阅
        userTopic1.removeListener(listenerId1);
        userTopic2.removeListener(listenerId2);
        userTopic3.removeListener(listenerId3);
        
        logger.info("=== 模式匹配订阅演示结束 ===\n");
    }
    
    /**
     * 消息确认和可靠性演示
     */
    public void demonstrateReliableMessaging() {
        logger.info("=== 可靠消息传递演示开始 ===");
        
        String topicName = "demo:reliable_topic";
        RTopic topic = client.getTopic(topicName);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        // 订阅并确认消息
        int listenerId = topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                logger.info("可靠接收消息: {}", msg);
                
                // 模拟消息处理
                try {
                    Thread.sleep(1000);
                    logger.info("消息处理完成: {}", msg);
                    latch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("消息处理被中断", e);
                }
            }
        });
        
        logger.info("已建立可靠消息订阅");
        
        // 发送重要消息
        long messageId = topic.publish("重要业务通知");
        logger.info("发送重要消息，消息ID: {}", messageId);
        
        try {
            if (latch.await(3, TimeUnit.SECONDS)) {
                logger.info("重要消息已确认接收和处理");
            } else {
                logger.warn("重要消息处理超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待可靠消息确认时被中断", e);
        }
        
        topic.removeListener(listenerId);
        logger.info("=== 可靠消息传递演示结束 ===\n");
    }
    
    /**
     * 性能测试演示
     */
    public void demonstratePerformanceTest() {
        logger.info("=== 消息性能测试开始 ===");
        
        String topicName = "demo:performance_topic";
        RTopic topic = client.getTopic(topicName);
        
        final int messageCount = 1000;
        CountDownLatch latch = new CountDownLatch(messageCount);
        long startTime = System.currentTimeMillis();
        
        // 订阅消息
        int listenerId = topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                // 简单处理，不记录日志以提高性能
                latch.countDown();
            }
        });
        
        // 快速发送大量消息
        for (int i = 0; i < messageCount; i++) {
            topic.publish("消息_" + i);
        }
        
        logger.info("已发送 {} 条消息", messageCount);
        
        try {
            if (latch.await(10, TimeUnit.SECONDS)) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                double qps = (messageCount * 1000.0) / duration;
                logger.info("性能测试结果:");
                logger.info("- 接收消息数量: {}", messageCount);
                logger.info("- 耗时: {} ms", duration);
                logger.info("- 平均 QPS: {:.2f}", qps);
            } else {
                logger.warn("性能测试未在预期时间内完成");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("性能测试被中断", e);
        }
        
        topic.removeListener(listenerId);
        logger.info("=== 消息性能测试结束 ===\n");
    }
    
    /**
     * 用户消息实体类
     */
    public static class UserMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String name;
        private String action;
        
        public UserMessage() {}
        
        public UserMessage(Long id, String name, String action) {
            this.id = id;
            this.name = name;
            this.action = action;
        }
        
        // getter 和 setter 方法
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        @Override
        public String toString() {
            return "UserMessage{id=" + id + ", name='" + name + "', action='" + action + "'}";
        }
    }
    
    /**
     * 运行所有发布订阅演示
     */
    public void runAllDemos() {
        logger.info("开始 Redisson 发布订阅演示...\n");
        
        demonstrateStringMessage();
        demonstrateObjectMessage();
        demonstrateMultipleTopics();
        demonstratePatternSubscription();
        demonstrateReliableMessaging();
        demonstratePerformanceTest();
        
        logger.info("Redisson 发布订阅演示完成！");
    }
}