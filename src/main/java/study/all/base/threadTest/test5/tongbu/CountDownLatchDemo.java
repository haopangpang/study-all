package study.all.base.threadTest.test5.tongbu;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch演示
 * 展示线程协调和等待机制
 */
public class CountDownLatchDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CountDownLatch线程协调演示 ===\n");
        
        // 创建一个计数为3的CountDownLatch
        CountDownLatch latch = new CountDownLatch(3);
        
        // 启动3个工作线程
        for (int i = 1; i <= 3; i++) {
            final int workerId = i;
            Thread worker = new Thread(() -> {
                try {
                    System.out.println("工作线程 " + workerId + " 开始执行任务...");
                    Thread.sleep((long) (Math.random() * 3000) + 1000); // 随机执行1-4秒
                    System.out.println("工作线程 " + workerId + " 完成任务!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // 完成任务后计数减1
                    System.out.println("工作线程 " + workerId + " 倒计时剩余: " + latch.getCount());
                }
            }, "Worker-" + i);
            
            worker.start();
        }
        
        System.out.println("主线程等待所有工作线程完成...");
        latch.await(); // 主线程等待直到计数为0
        System.out.println("所有工作线程已完成，主线程继续执行!");
    }
}