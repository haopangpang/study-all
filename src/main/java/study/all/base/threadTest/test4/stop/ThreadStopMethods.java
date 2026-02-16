package study.all.base.threadTest.test4.stop;


/**
 * 线程停止方法演示
 * 展示各种线程停止方式的使用和特点
 */
public class ThreadStopMethods {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 线程停止方法演示 ===\n");
        
        // 演示1: interrupt()中断机制
        demonstrateInterruptMethod();
        
        Thread.sleep(1000);
        
        // 演示2: 标志位控制
        demonstrateFlagControl();
        
        Thread.sleep(1000);
        
        // 演示3: 守护线程
        demonstrateDaemonThread();
        
        Thread.sleep(1000);
        
        // 演示4: 不安全的stop()方法
        demonstrateUnsafeStop();
        
        Thread.sleep(1000);
        
        // 演示5: 综合最佳实践
        demonstrateBestPractices();
    }
    
    /**
     * interrupt()中断机制演示
     */
    private static void demonstrateInterruptMethod() throws InterruptedException {
        System.out.println("--- interrupt()中断机制 ---");
        
        Thread interruptibleThread = new Thread(() -> {
            System.out.println("可中断线程开始执行");
            
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("工作中...");
                    Thread.sleep(500); // 可中断的阻塞操作
                } catch (InterruptedException e) {
                    System.out.println("检测到中断信号");
                    // 重要：捕获InterruptedException后，中断状态被清除
                    // 需要重新设置中断状态或跳出循环
                    Thread.currentThread().interrupt(); // 重新设置中断状态
                    break;
                }
            }
            
            System.out.println("线程正常退出");
        }, "InterruptibleThread");
        
        interruptibleThread.start();
        
        // 让线程运行一段时间
        Thread.sleep(2000);
        
        System.out.println("发送中断信号...");
        interruptibleThread.interrupt();
        
        interruptibleThread.join();
        System.out.println("中断机制演示完成\n");
    }
    
    /**
     * 标志位控制演示
     */
    private static void demonstrateFlagControl() throws InterruptedException {
        System.out.println("--- 标志位控制方式 ---");
        
        class FlagControlledTask implements Runnable {
            private volatile boolean running = true;
            
            @Override
            public void run() {
                System.out.println("标志控制线程开始");
                
                while (running) {
                    System.out.println("执行任务中...");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        System.out.println("睡眠被中断");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                System.out.println("标志控制线程结束");
            }
            
            public void stop() {
                running = false;
                System.out.println("设置停止标志");
            }
        }
        
        FlagControlledTask task = new FlagControlledTask();
        Thread flagThread = new Thread(task, "FlagControlledThread");
        
        flagThread.start();
        
        Thread.sleep(1500);
        task.stop(); // 设置停止标志
        
        flagThread.join();
        System.out.println("标志位控制演示完成\n");
    }
    
    /**
     * 守护线程演示
     */
    private static void demonstrateDaemonThread() throws InterruptedException {
        System.out.println("--- 守护线程机制 ---");
        
        Thread daemonWorker = new Thread(() -> {
            while (true) {
                System.out.println("守护线程工作中...");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    System.out.println("守护线程被中断");
                    break;
                }
            }
        }, "DaemonWorker");
        
        daemonWorker.setDaemon(true); // 设置为守护线程
        System.out.println("守护线程已设置");
        
        daemonWorker.start();
        
        System.out.println("主线程继续执行3秒后结束...");
        Thread.sleep(3000);
        
        System.out.println("主线程结束，守护线程将自动终止");
        System.out.println("守护线程演示完成\n");
    }
    
    /**
     * 不安全的stop()方法演示
     */
    private static void demonstrateUnsafeStop() throws InterruptedException {
        System.out.println("--- 不安全的stop()方法（仅供演示） ---");
        
        Thread unsafeThread = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("不安全线程执行中...");
                    Thread.sleep(200);
                }
            } catch (ThreadDeath td) {
                System.out.println("线程被强制终止！");
                // ThreadDeath是stop()方法抛出的错误
                throw td;
            } catch (InterruptedException e) {
                System.out.println("线程被中断");
                Thread.currentThread().interrupt();
            }
        }, "UnsafeThread");
        
        unsafeThread.start();
        
        Thread.sleep(1000);
        
        System.out.println("警告：使用已废弃的stop()方法");
        unsafeThread.stop(); // 不推荐使用
        
        unsafeThread.join();
        System.out.println("不安全停止演示完成\n");
    }
    
    /**
     * 综合最佳实践演示
     */
    private static void demonstrateBestPractices() throws InterruptedException {
        System.out.println("--- 线程停止最佳实践 ---");
        
        class BestPracticeTask implements Runnable {
            private volatile boolean shutdown = false;
            private final Object lock = new Object();
            
            @Override
            public void run() {
                System.out.println("最佳实践线程启动");
                
                try {
                    while (!shutdown && !Thread.currentThread().isInterrupted()) {
                        // 检查多种停止条件
                        synchronized (lock) {
                            if (shutdown) break;
                            
                            System.out.println("最佳实践：执行业务逻辑");
                            
                            // 模拟长时间操作
                            lock.wait(800); // 可中断的等待
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("最佳实践：检测到中断");
                    Thread.currentThread().interrupt();
                } finally {
                    cleanup(); // 清理资源
                }
                
                System.out.println("最佳实践线程优雅退出");
            }
            
            public void requestShutdown() {
                synchronized (lock) {
                    shutdown = true;
                    lock.notifyAll(); // 唤醒等待的线程
                }
                System.out.println("最佳实践：请求关闭");
            }
            
            private void cleanup() {
                System.out.println("最佳实践：执行清理工作");
                // 释放资源、关闭连接等
            }
        }
        
        BestPracticeTask task = new BestPracticeTask();
        Thread bestThread = new Thread(task, "BestPracticeThread");
        
        bestThread.start();
        
        Thread.sleep(2000);
        task.requestShutdown();
        
        bestThread.join(3000); // 最多等待3秒
        
        if (bestThread.isAlive()) {
            System.out.println("线程未及时停止，发送中断");
            bestThread.interrupt();
            bestThread.join();
        }
        
        System.out.println("最佳实践演示完成\n");
    }
    
    /**
     * 线程池优雅关闭演示
     */
    public static void demonstrateThreadPoolShutdown() {
        System.out.println("--- 线程池优雅关闭 ---");
        
        java.util.concurrent.ExecutorService executor = 
            java.util.concurrent.Executors.newFixedThreadPool(3);
        
        // 提交任务
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("任务" + taskId + "开始执行");
                    Thread.sleep(1000);
                    System.out.println("任务" + taskId + "执行完成");
                } catch (InterruptedException e) {
                    System.out.println("任务" + taskId + "被中断");
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // 优雅关闭
        System.out.println("请求线程池关闭...");
        executor.shutdown(); // 不再接受新任务，已提交任务继续执行
        
        try {
            // 等待最多5秒
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                System.out.println("超时，强制关闭");
                java.util.List<Runnable> remaining = executor.shutdownNow();
                System.out.println("剩余未执行任务数: " + remaining.size());
            }
        } catch (InterruptedException e) {
            System.out.println("等待关闭时被中断");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("线程池关闭演示完成\n");
    }
}