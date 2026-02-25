package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.all.redisson.config.RedissonConfig;
import study.all.redisson.demo.BasicOperationsDemo;
import study.all.redisson.demo.DistributedLockDemo;
import study.all.redisson.demo.PubSubDemo;

/**
 * Redisson 入门 Demo 主程序
 * 整合所有演示功能并提供交互式菜单
 */
public class RedissonDemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(RedissonDemoApplication.class);
    
    public static void main(String[] args) {
        logger.info("=========================================");
        logger.info("      Redisson 入门演示程序启动");
        logger.info("=========================================\n");
        
        try {
            // 检查 Redisson 客户端连接
            if (!RedissonConfig.isAvailable()) {
                logger.error("Redisson 客户端不可用，请确保 Redis 服务正在运行！");
                logger.error("默认连接地址: redis://127.0.0.1:6379");
                return;
            }
            
            logger.info("Redisson 客户端连接正常\n");
            
            // 创建各个演示实例
            BasicOperationsDemo basicDemo = new BasicOperationsDemo();
            DistributedLockDemo lockDemo = new DistributedLockDemo();
            PubSubDemo pubSubDemo = new PubSubDemo();
            
            // 显示菜单并执行选择的功能
            showMenuAndRun(basicDemo, lockDemo, pubSubDemo);
            
        } catch (Exception e) {
            logger.error("程序运行出错", e);
        } finally {
            // 程序结束时关闭 Redisson 客户端
            RedissonConfig.shutdown();
            logger.info("\n程序已安全退出");
        }
    }
    
    /**
     * 显示菜单并根据用户选择执行相应功能
     */
    private static void showMenuAndRun(BasicOperationsDemo basicDemo, 
                                     DistributedLockDemo lockDemo, 
                                     PubSubDemo pubSubDemo) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        while (true) {
            printMenu();
            System.out.print("请选择功能 (输入对应数字): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
                
                switch (choice) {
                    case 1:
                        System.out.println("\n--- 执行基础操作演示 ---");
                        basicDemo.runAllDemos();
                        break;
                        
                    case 2:
                        System.out.println("\n--- 执行分布式锁演示 ---");
                        lockDemo.runAllDemos();
                        break;
                        
                    case 3:
                        System.out.println("\n--- 执行发布订阅演示 ---");
                        pubSubDemo.runAllDemos();
                        break;
                        
                    case 4:
                        System.out.println("\n--- 执行所有演示 ---");
                        runAllDemos(basicDemo, lockDemo, pubSubDemo);
                        break;
                        
                    case 0:
                        System.out.println("感谢使用 Redisson 演示程序！");
                        return;
                        
                    default:
                        System.out.println("无效的选择，请重新输入！");
                }
                
                System.out.println("\n按回车键继续...");
                scanner.nextLine();
                
            } catch (Exception e) {
                logger.error("执行过程中发生错误", e);
                scanner.nextLine(); // 清除错误输入
            }
        }
    }
    
    /**
     * 打印主菜单
     */
    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println("         Redisson 功能演示菜单");
        System.out.println("=========================================");
        System.out.println("1. 基础数据结构操作演示");
        System.out.println("   - RMap (分布式 Map)");
        System.out.println("   - RList (分布式 List)");
        System.out.println("   - RSet (分布式 Set)");
        System.out.println("   - AtomicLong (原子操作)");
        System.out.println("   - 带过期时间的数据结构");
        System.out.println();
        System.out.println("2. 分布式锁演示");
        System.out.println("   - 可重入锁");
        System.out.println("   - 带超时锁");
        System.out.println("   - 公平锁");
        System.out.println("   - 读写锁");
        System.out.println("   - 多线程锁竞争");
        System.out.println("   - 锁自动续期");
        System.out.println();
        System.out.println("3. 发布订阅演示");
        System.out.println("   - 字符串消息传递");
        System.out.println("   - 对象消息传递");
        System.out.println("   - 多频道订阅");
        System.out.println("   - 模式匹配订阅");
        System.out.println("   - 可靠消息传递");
        System.out.println("   - 性能测试");
        System.out.println();
        System.out.println("4. 执行所有演示");
        System.out.println("0. 退出程序");
        System.out.println("=========================================");
    }
    
    /**
     * 执行所有演示功能
     */
    private static void runAllDemos(BasicOperationsDemo basicDemo, 
                                  DistributedLockDemo lockDemo, 
                                  PubSubDemo pubSubDemo) {
        logger.info("开始执行所有 Redisson 演示...\n");
        
        try {
            // 执行基础操作演示
            System.out.println("1. 执行基础操作演示...");
            basicDemo.runAllDemos();
            Thread.sleep(1000);
            
            // 执行分布式锁演示
            System.out.println("2. 执行分布式锁演示...");
            lockDemo.runAllDemos();
            Thread.sleep(1000);
            
            // 执行发布订阅演示
            System.out.println("3. 执行发布订阅演示...");
            pubSubDemo.runAllDemos();
            
            logger.info("\n所有演示执行完成！");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("演示执行被中断", e);
        }
    }
}