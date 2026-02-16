package study.all.base.threadTest.test3.api;

import java.util.*;
import java.util.concurrent.*;

/**
 * 线程方法对比测试工具
 * 量化分析sleep、wait、join、yield的行为特征
 */
public class ThreadMethodAnalyzer {
    
    private static final Object SHARED_LOCK = new Object();
    private static volatile boolean signal = false;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 线程方法行为分析工具 ===\n");
        
        analyzeSleepBehavior();
        Thread.sleep(1000);
        
        analyzeWaitBehavior();
        Thread.sleep(1000);
        
        analyzeJoinBehavior();
        Thread.sleep(1000);
        
        analyzeYieldBehavior();
        Thread.sleep(1000);
        
        performanceComparison();
    }
    
    /**
     * 分析sleep()方法行为
     */
    private static void analyzeSleepBehavior() throws InterruptedException {
        System.out.println("--- sleep()方法行为分析 ---");
        
        MethodMetrics metrics = new MethodMetrics("sleep");
        
        Thread sleeper = new Thread(() -> {
            try {
                long startTime = System.nanoTime();
                metrics.recordStateChange("开始睡眠");
                Thread.sleep(1000);
                long actualDuration = System.nanoTime() - startTime;
                metrics.recordStateChange("睡眠结束");
                metrics.actualDuration = actualDuration / 1_000_000; // 转换为毫秒
            } catch (InterruptedException e) {
                metrics.recordStateChange("睡眠被中断");
                Thread.currentThread().interrupt();
            }
        });
        
        metrics.startMonitoring(sleeper);
        sleeper.start();
        sleeper.join();
        
        metrics.printAnalysis();
        System.out.println();
    }
    
    /**
     * 分析wait()方法行为
     */
    private static void analyzeWaitBehavior() throws InterruptedException {
        System.out.println("--- wait()方法行为分析 ---");
        
        MethodMetrics metrics = new MethodMetrics("wait");
        
        Thread waiter = new Thread(() -> {
            synchronized (SHARED_LOCK) {
                try {
                    metrics.recordStateChange("获得锁，准备等待");
                    long startTime = System.nanoTime();
                    SHARED_LOCK.wait(1500); // 最多等待1.5秒
                    long waitDuration = System.nanoTime() - startTime;
                    metrics.actualDuration = waitDuration / 1_000_000;
                    metrics.recordStateChange("等待结束");
                } catch (InterruptedException e) {
                    metrics.recordStateChange("等待被中断");
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread notifier = new Thread(() -> {
            try {
                Thread.sleep(800); // 800ms后唤醒
                synchronized (SHARED_LOCK) {
                    metrics.recordStateChange("发送通知");
                    SHARED_LOCK.notify();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        metrics.startMonitoring(waiter);
        waiter.start();
        notifier.start();
        
        waiter.join();
        notifier.join();
        
        metrics.printAnalysis();
        System.out.println();
    }
    
    /**
     * 分析join()方法行为
     */
    private static void analyzeJoinBehavior() throws InterruptedException {
        System.out.println("--- join()方法行为分析 ---");
        
        MethodMetrics metrics = new MethodMetrics("join");
        
        Thread target = new Thread(() -> {
            try {
                metrics.recordStateChange("目标线程开始执行");
                Thread.sleep(1200);
                metrics.recordStateChange("目标线程执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread joiner = new Thread(() -> {
            try {
                metrics.recordStateChange("开始等待目标线程");
                long startTime = System.nanoTime();
                target.join(2000); // 最多等待2秒
                long waitDuration = System.nanoTime() - startTime;
                metrics.actualDuration = waitDuration / 1_000_000;
                metrics.recordStateChange("等待结束");
            } catch (InterruptedException e) {
                metrics.recordStateChange("等待被中断");
                Thread.currentThread().interrupt();
            }
        });
        
        metrics.startMonitoring(joiner);
        target.start();
        joiner.start();
        
        joiner.join();
        target.join();
        
        metrics.printAnalysis();
        System.out.println();
    }
    
    /**
     * 分析yield()方法行为
     */
    private static void analyzeYieldBehavior() {
        System.out.println("--- yield()方法行为分析 ---");
        
        MethodMetrics metrics = new MethodMetrics("yield");
        
        class YieldTester implements Runnable {
            private final String name;
            private final int iterations;
            
            public YieldTester(String name, int iterations) {
                this.name = name;
                this.iterations = iterations;
            }
            
            @Override
            public void run() {
                metrics.recordStateChange(name + " 开始执行");
                int yieldCount = 0;
                
                for (int i = 0; i < iterations; i++) {
                    if (i % 3 == 0) {
                        Thread.yield();
                        yieldCount++;
                    }
                    // 模拟少量工作
                    Math.sqrt(i);
                }
                
                metrics.recordStateChange(name + " 执行完成，让出" + yieldCount + "次");
            }
        }
        
        Thread task1 = new Thread(new YieldTester("任务A", 100000));
        Thread task2 = new Thread(new YieldTester("任务B", 100000));
        
        metrics.startMonitoring(task1);
        metrics.startMonitoring(task2);
        
        long startTime = System.currentTimeMillis();
        task1.start();
        task2.start();
        
        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        metrics.actualDuration = System.currentTimeMillis() - startTime;
        metrics.printAnalysis();
        System.out.println();
    }
    
    /**
     * 性能对比测试
     */
    private static void performanceComparison() throws InterruptedException {
        System.out.println("=== 性能对比测试 ===");
        
        final int TEST_ITERATIONS = 1000;
        
        // 测试sleep开销
        long sleepStart = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Thread.sleep(1); // 1ms sleep
        }
        long sleepTime = System.nanoTime() - sleepStart;
        
        // 测试yield开销
        long yieldStart = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Thread.yield();
        }
        long yieldTime = System.nanoTime() - yieldStart;
        
        // 测试busy wait开销（作为对比）
        long busyStart = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            // 空循环
            for (int j = 0; j < 1000; j++) {
                Math.sqrt(j);
            }
        }
        long busyTime = System.nanoTime() - busyStart;
        
        System.out.println("性能对比结果 (" + TEST_ITERATIONS + " 次调用):");
        System.out.printf("sleep(1ms):  %.2f ms 总耗时%n", sleepTime / 1_000_000.0);
        System.out.printf("yield():     %.2f ms 总耗时%n", yieldTime / 1_000_000.0);
        System.out.printf("busy wait:   %.2f ms 总耗时%n", busyTime / 1_000_000.0);
        System.out.printf("sleep vs yield 性能比: %.2f%n", (double)sleepTime / yieldTime);
        System.out.println();
    }
    
    /**
     * 方法行为度量类
     */
    static class MethodMetrics {
        private final String methodName;
        private final List<StateRecord> stateChanges = new ArrayList<>();
        private long actualDuration;
        private final Map<Thread, StateMonitor> monitors = new HashMap<>();
        
        public MethodMetrics(String methodName) {
            this.methodName = methodName;
        }
        
        public void recordStateChange(String description) {
            stateChanges.add(new StateRecord(
                System.currentTimeMillis(),
                Thread.currentThread().getName(),
                Thread.currentThread().getState(),
                description
            ));
        }
        
        public void startMonitoring(Thread thread) {
            StateMonitor monitor = new StateMonitor(thread, this);
            monitors.put(thread, monitor);
            monitor.start();
        }
        
        public void printAnalysis() {
            System.out.println("方法: " + methodName);
            System.out.println("预期行为持续时间: " + actualDuration + "ms");
            System.out.println("状态变化记录:");
            
            for (StateRecord record : stateChanges) {
                System.out.printf("  [%tT] %s (%s): %s%n",
                    record.timestamp, record.threadName, record.state, record.description);
            }
            
            // 分析锁行为
            analyzeLockBehavior();
        }
        
        private void analyzeLockBehavior() {
            boolean involvesLock = methodName.equals("wait");
            boolean releasesLock = methodName.equals("wait");
            boolean waitsForOther = methodName.equals("join") || methodName.equals("wait");
            
            System.out.println("行为特征分析:");
            System.out.println("  - 涉及锁机制: " + (involvesLock ? "是" : "否"));
            System.out.println("  - 释放锁资源: " + (releasesLock ? "是" : "否"));
            System.out.println("  - 等待其他线程: " + (waitsForOther ? "是" : "否"));
            System.out.println("  - 可被中断: " + (!methodName.equals("yield") ? "是" : "否"));
        }
    }
    
    /**
     * 状态记录
     */
    static class StateRecord {
        final long timestamp;
        final String threadName;
        final Thread.State state;
        final String description;
        
        StateRecord(long timestamp, String threadName, Thread.State state, String description) {
            this.timestamp = timestamp;
            this.threadName = threadName;
            this.state = state;
            this.description = description;
        }
    }
    
    /**
     * 状态监控器
     */
    static class StateMonitor extends Thread {
        private final Thread targetThread;
        private final MethodMetrics metrics;
        private Thread.State lastState = Thread.State.NEW;
        
        StateMonitor(Thread targetThread, MethodMetrics metrics) {
            this.targetThread = targetThread;
            this.metrics = metrics;
            setDaemon(true);
        }
        
        @Override
        public void run() {
            while (targetThread.isAlive()) {
                Thread.State currentState = targetThread.getState();
                if (currentState != lastState) {
                    metrics.stateChanges.add(new StateRecord(
                        System.currentTimeMillis(),
                        targetThread.getName(),
                        currentState,
                        "状态变更: " + lastState + " → " + currentState
                    ));
                    lastState = currentState;
                }
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * 实用工具方法
     */
    public static class ThreadUtils {
        
        /**
         * 安全的sleep方法
         */
        public static void safeSleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("睡眠被中断", e);
            }
        }
        
        /**
         * 带超时的安全join
         */
        public static boolean safeJoin(Thread thread, long timeoutMillis) {
            if (thread == null) return true;
            
            try {
                thread.join(timeoutMillis);
                return true; // join成功完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false; // 被中断
            }
        }
        
        /**
         * 安全的wait方法
         */
        public static void safeWait(Object lock, long timeoutMillis) {
            try {
                if (timeoutMillis > 0) {
                    lock.wait(timeoutMillis);
                } else {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("等待被中断", e);
            }
        }
    }
}