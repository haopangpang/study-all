package study.all.redisson.demo;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.all.redisson.config.RedissonConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁演示类
 * 展示各种分布式锁的使用方式
 */
public class DistributedLockDemo {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockDemo.class);
    private final RedissonClient client;
    
    public DistributedLockDemo() {
        this.client = RedissonConfig.getClient();
    }
    
    /**
     * 演示基本的可重入锁
     */
    public void demonstrateReentrantLock() {
        logger.info("=== 可重入锁演示开始 ===");
        
        String lockName = "demo:reentrant_lock";
        RLock lock = client.getLock(lockName);
        
        try {
            // 第一次获取锁
            lock.lock();
            logger.info("第一次获取锁成功");
            
            // 在同一个线程中可以重复获取锁（可重入性）
            lock.lock();
            logger.info("第二次获取锁成功（可重入）");
            
            // 执行业务逻辑
            logger.info("执行需要同步保护的业务逻辑...");
            Thread.sleep(2000);
            
            // 释放锁（需要释放两次，因为获取了两次）
            lock.unlock();
            logger.info("第一次释放锁");
            lock.unlock();
            logger.info("第二次释放锁");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("线程被中断", e);
        } finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        
        logger.info("=== 可重入锁演示结束 ===\n");
    }
    
    /**
     * 演示带超时的锁
     */
    public void demonstrateLockWithTimeout() {
        logger.info("=== 带超时锁演示开始 ===");
        
        String lockName = "demo:timeout_lock";
        RLock lock = client.getLock(lockName);
        
        try {
            // 尝试获取锁，最多等待3秒，持有锁10秒后自动释放
            boolean acquired = lock.tryLock(3, 10, TimeUnit.SECONDS);
            
            if (acquired) {
                logger.info("成功获取带超时的锁");
                try {
                    // 模拟长时间业务处理
                    logger.info("执行业务逻辑...");
                    Thread.sleep(5000);
                } finally {
                    lock.unlock();
                    logger.info("释放锁");
                }
            } else {
                logger.info("未能在指定时间内获取锁");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("线程被中断", e);
        }
        
        logger.info("=== 带超时锁演示结束 ===\n");
    }
    
    /**
     * 演示公平锁
     */
    public void demonstrateFairLock() {
        logger.info("=== 公平锁演示开始 ===");
        
        String lockName = "demo:fair_lock";
        RLock fairLock = client.getFairLock(lockName);
        
        try {
            fairLock.lock();
            logger.info("获取公平锁成功");
            
            // 执行业务逻辑
            logger.info("执行公平锁保护的业务逻辑...");
            Thread.sleep(3000);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("线程被中断", e);
        } finally {
            if (fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
                logger.info("释放公平锁");
            }
        }
        
        logger.info("=== 公平锁演示结束 ===\n");
    }
    
    /**
     * 演示读写锁
     */
    public void demonstrateReadWriteLock() {
        logger.info("=== 读写锁演示开始 ===");
        
        String lockName = "demo:rw_lock";
        RLock readLock = client.getReadWriteLock(lockName).readLock();
        RLock writeLock = client.getReadWriteLock(lockName).writeLock();
        
        // 模拟多个读操作并发执行
        CountDownLatch readLatch = new CountDownLatch(3);
        
        // 启动读线程
        for (int i = 0; i < 3; i++) {
            final int threadNum = i;
            new Thread(() -> {
                try {
                    readLock.lock();
                    logger.info("读线程 {} 获取读锁成功", threadNum);
                    
                    // 模拟读操作
                    Thread.sleep(2000);
                    logger.info("读线程 {} 执行读操作", threadNum);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    readLock.unlock();
                    logger.info("读线程 {} 释放读锁", threadNum);
                    readLatch.countDown();
                }
            }).start();
        }
        
        // 等待一段时间后启动写操作
        try {
            Thread.sleep(500);
            
            // 写操作需要等待所有读操作完成
            new Thread(() -> {
                try {
                    writeLock.lock();
                    logger.info("写线程获取写锁成功");
                    
                    // 写操作会阻塞后续的读操作
                    Thread.sleep(3000);
                    logger.info("写线程执行写操作");
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    writeLock.unlock();
                    logger.info("写线程释放写锁");
                }
            }).start();
            
            readLatch.await();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("主线程被中断", e);
        }
        
        logger.info("=== 读写锁演示结束 ===\n");
    }
    
    /**
     * 演示多线程竞争锁的场景
     */
    public void demonstrateMultiThreadCompetition() {
        logger.info("=== 多线程锁竞争演示开始 ===");
        
        String lockName = "demo:competition_lock";
        CountDownLatch latch = new CountDownLatch(5);
        
        // 创建5个竞争线程
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                RLock lock = client.getLock(lockName);
                try {
                    // 尝试获取锁，等待最多2秒
                    if (lock.tryLock(2, TimeUnit.SECONDS)) {
                        try {
                            logger.info("线程 {} 成功获取锁", threadId);
                            
                            // 模拟业务处理
                            Thread.sleep(1000);
                            logger.info("线程 {} 执行业务逻辑", threadId);
                            
                        } finally {
                            lock.unlock();
                            logger.info("线程 {} 释放锁", threadId);
                        }
                    } else {
                        logger.info("线程 {} 未能获取锁", threadId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("线程 {} 被中断", threadId, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("主线程被中断", e);
        }
        
        logger.info("=== 多线程锁竞争演示结束 ===\n");
    }
    
    /**
     * 演示锁的续期机制
     */
    public void demonstrateLeaseRenewal() {
        logger.info("=== 锁续期演示开始 ===");
        
        String lockName = "demo:lease_lock";
        RLock lock = client.getLock(lockName);
        
        try {
            // 获取锁，持有时间设置为较短的时间
            lock.lock(3, TimeUnit.SECONDS);
            logger.info("获取锁成功，初始租约时间为3秒");
            
            // 模拟长时间业务处理（超过初始租约时间）
            for (int i = 0; i < 5; i++) {
                logger.info("执行第 {} 轮业务处理...", i + 1);
                Thread.sleep(2000);
            }
            
            logger.info("业务处理完成");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("线程被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                logger.info("释放锁");
            }
        }
        
        logger.info("=== 锁续期演示结束 ===\n");
    }
    
    /**
     * 运行所有锁演示
     */
    public void runAllDemos() {
        logger.info("开始 Redisson 分布式锁演示...\n");
        
        demonstrateReentrantLock();
        demonstrateLockWithTimeout();
        demonstrateFairLock();
        demonstrateReadWriteLock();
        demonstrateMultiThreadCompetition();
        demonstrateLeaseRenewal();
        
        logger.info("Redisson 分布式锁演示完成！");
    }
}