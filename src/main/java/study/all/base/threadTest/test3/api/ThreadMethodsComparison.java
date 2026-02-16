package study.all.base.threadTest.test3.api;

/**
 * 线程API方法对比演示
 * 详细展示sleep、wait、join、yield的区别和使用场景
 */
public class ThreadMethodsComparison {
    
    private static final Object LOCK = new Object();
    private static boolean ready = false;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java线程API方法对比演示 ===\n");
        
        // 演示1: sleep()方法
        demonstrateSleep();
        
        Thread.sleep(1000);
        
        // 演示2: wait()/notify()机制
        demonstrateWaitNotify();
        
        Thread.sleep(1000);
        
        // 演示3: join()方法
        demonstrateJoin();
        
        Thread.sleep(1000);
        
        // 演示4: yield()方法
        demonstrateYield();
        
        Thread.sleep(1000);
        
        // 演示5: 综合对比
        demonstrateComprehensiveComparison();
    }
    
    /**
     * sleep()方法演示
     */
    private static void demonstrateSleep() {
        System.out.println("--- sleep()方法演示 ---");
        
        Thread sleeper = new Thread(() -> {
            System.out.println("睡眠线程开始，当前状态: " + Thread.currentThread().getState());
            try {
                System.out.println("开始睡眠2秒...");
                long startTime = System.currentTimeMillis();
                Thread.sleep(2000);
                long endTime = System.currentTimeMillis();
                System.out.println("睡眠结束，实际耗时: " + (endTime - startTime) + "ms");
                System.out.println("睡眠后状态: " + Thread.currentThread().getState());
            } catch (InterruptedException e) {
                System.out.println("睡眠被中断");
                Thread.currentThread().interrupt();
            }
        }, "SleepThread");
        
        sleeper.start();
        
        // 监控线程状态
        monitorThreadState(sleeper, "sleep演示");
        
        try {
            sleeper.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }
    
    /**
     * wait()/notify()机制演示
     */
    private static void demonstrateWaitNotify() {
        System.out.println("--- wait()/notify()机制演示 ---");
        
        Thread waiter = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("等待线程获得锁，准备等待...");
                try {
                    System.out.println("调用wait前状态: " + Thread.currentThread().getState());
                    long startTime = System.currentTimeMillis();
                    LOCK.wait(); // 释放锁并等待
                    long waitTime = System.currentTimeMillis() - startTime;
                    System.out.println("被唤醒，等待时间: " + waitTime + "ms");
                    System.out.println("唤醒后状态: " + Thread.currentThread().getState());
                } catch (InterruptedException e) {
                    System.out.println("等待被中断");
                    Thread.currentThread().interrupt();
                }
            }
        }, "WaitThread");
        
        Thread notifier = new Thread(() -> {
            try {
                Thread.sleep(1000); // 让等待线程先执行
                synchronized (LOCK) {
                    System.out.println("通知线程获得锁，准备通知...");
                    LOCK.notify(); // 唤醒等待线程
                    System.out.println("已发送通知");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "NotifyThread");
        
        waiter.start();
        notifier.start();
        
        try {
            waiter.join();
            notifier.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }
    
    /**
     * join()方法演示
     */
    private static void demonstrateJoin() throws InterruptedException {
        System.out.println("--- join()方法演示 ---");
        
        Thread worker = new Thread(() -> {
            System.out.println("工作线程开始执行...");
            try {
                Thread.sleep(1500);
                System.out.println("工作线程完成任务");
            } catch (InterruptedException e) {
                System.out.println("工作线程被中断");
                Thread.currentThread().interrupt();
            }
        }, "WorkerThread");
        
        System.out.println("主线程启动工作线程");
        worker.start();
        
        System.out.println("主线程调用join()等待...");
        long startTime = System.currentTimeMillis();
        worker.join();
        long waitTime = System.currentTimeMillis() - startTime;
        
        System.out.println("主线程继续执行，等待时间: " + waitTime + "ms");
        System.out.println("工作线程最终状态: " + worker.getState());
        System.out.println();
    }
    
    /**
     * yield()方法演示
     */
    private static void demonstrateYield() {
        System.out.println("--- yield()方法演示 ---");
        
        class YieldTask implements Runnable {
            private final String name;
            private final int yieldFrequency;
            
            public YieldTask(String name, int yieldFrequency) {
                this.name = name;
                this.yieldFrequency = yieldFrequency;
            }
            
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(name + " 执行第 " + (i + 1) + " 次");
                    if (i % yieldFrequency == 0) {
                        System.out.println(name + " 主动让出CPU");
                        Thread.yield();
                    }
                    
                    // 模拟一些工作
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println(name + " 执行完成");
            }
        }
        
        Thread task1 = new Thread(new YieldTask("任务A(频繁让出)", 2), "Task-A");
        Thread task2 = new Thread(new YieldTask("任务B(偶尔让出)", 5), "Task-B");
        Thread task3 = new Thread(new YieldTask("任务C(从不让出)", 20), "Task-C");
        
        task1.start();
        task2.start();
        task3.start();
        
        try {
            task1.join();
            task2.join();
            task3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }
    
    /**
     * 综合对比演示
     */
    private static void demonstrateComprehensiveComparison() throws InterruptedException {
        System.out.println("=== 四种方法综合对比 ===");
        
        // 创建测试线程
        Thread[] testThreads = new Thread[4];
        
        // sleep测试
        testThreads[0] = new Thread(() -> {
            try {
                System.out.println("[sleep] 开始睡眠1秒");
                Thread.sleep(1000);
                System.out.println("[sleep] 睡眠结束");
            } catch (InterruptedException e) {
                System.out.println("[sleep] 被中断");
            }
        }, "SleepTest");
        
        // wait测试
        testThreads[1] = new Thread(() -> {
            synchronized (LOCK) {
                try {
                    System.out.println("[wait] 开始等待");
                    LOCK.wait(1500); // 最多等待1.5秒
                    System.out.println("[wait] 等待结束");
                } catch (InterruptedException e) {
                    System.out.println("[wait] 被中断");
                }
            }
        }, "WaitTest");
        
        // join测试
        Thread targetThread = new Thread(() -> {
            try {
                Thread.sleep(800);
                System.out.println("[target] 目标线程完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "TargetThread");
        
        testThreads[2] = new Thread(() -> {
            try {
                System.out.println("[join] 开始等待目标线程");
                targetThread.join(1200); // 最多等待1.2秒
                System.out.println("[join] 等待结束");
            } catch (InterruptedException e) {
                System.out.println("[join] 被中断");
            }
        }, "JoinTest");
        
        // yield测试
        testThreads[3] = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("[yield] 执行循环 " + (i + 1));
                Thread.yield();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("[yield] 执行完成");
        }, "YieldTest");
        
        // 启动所有测试
        System.out.println("启动所有测试线程...\n");
        
        targetThread.start();
        for (Thread thread : testThreads) {
            thread.start();
        }
        
        // 等待所有测试完成
        for (Thread thread : testThreads) {
            thread.join();
        }
        targetThread.join();
        
        System.out.println("\n=== 对比总结 ===");
        System.out.println("sleep():  当前线程暂停执行，不释放任何锁");
        System.out.println("wait():   当前线程释放对象锁并等待，需要notify唤醒");
        System.out.println("join():   当前线程等待目标线程完成");
        System.out.println("yield():  当前线程主动让出CPU执行权，不释放锁");
    }
    
    /**
     * 监控线程状态变化
     */
    private static void monitorThreadState(Thread thread, String description) {
        new Thread(() -> {
            Thread.State lastState = Thread.State.NEW;
            long startTime = System.currentTimeMillis();
            
            while (thread.isAlive()) {
                Thread.State currentState = thread.getState();
                if (currentState != lastState) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.println(String.format("[%dms] %s 状态变化: %s → %s", 
                        elapsed, description, lastState, currentState));
                    lastState = currentState;
                }
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Monitor-" + description).start();
    }
}