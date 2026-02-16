package study.all.base.threadTest.test5.tongbu;

/**
 * 线程同步演示类
 * 展示不同场景下的线程同步问题和解决方案
 */
public class ThreadSynchronizationDemo {
    
    // 共享资源 - 银行账户余额
    private static int balance = 1000;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 线程同步概念演示 ===\n");
        
        // 演示1：无同步的问题
        demonstrateRaceCondition();
        
        // 演示2：使用synchronized关键字解决
        demonstrateSynchronizedSolution();
        
        // 演示3：使用Lock接口解决
        demonstrateLockSolution();
    }
    
    /**
     * 演示竞态条件（Race Condition）
     * 多个线程同时访问共享资源时可能出现的数据不一致问题
     */
    private static void demonstrateRaceCondition() throws InterruptedException {
        System.out.println("1. 竞态条件演示（无同步）:");
        balance = 1000;
        
        Thread withdrawThread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                withdraw(10); // 取款10元
            }
        });
        
        Thread withdrawThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                withdraw(10); // 取款10元
            }
        });
        
        withdrawThread1.start();
        withdrawThread2.start();
        
        withdrawThread1.join();
        withdrawThread2.join();
        
        System.out.println("期望余额: " + (1000 - 2000)); // 应该是-1000
        System.out.println("实际余额: " + balance);
        System.out.println("差额: " + (balance - (1000 - 2000)) + "\n");
    }
    
    /**
     * 演示synchronized关键字解决方案
     */
    private static void demonstrateSynchronizedSolution() throws InterruptedException {
        System.out.println("2. synchronized关键字解决方案:");
        balance = 1000;
        
        Thread withdrawThread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronizedWithdraw(10);
            }
        });
        
        Thread withdrawThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronizedWithdraw(10);
            }
        });
        
        withdrawThread1.start();
        withdrawThread2.start();
        
        withdrawThread1.join();
        withdrawThread2.join();
        
        System.out.println("期望余额: " + (1000 - 2000));
        System.out.println("实际余额: " + balance);
        System.out.println("结果正确: " + (balance == (1000 - 2000)) + "\n");
    }
    
    /**
     * 演示Lock接口解决方案
     */
    private static void demonstrateLockSolution() throws InterruptedException {
        System.out.println("3. Lock接口解决方案:");
        balance = 1000;
        java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
        
        Thread withdrawThread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                lockWithdraw(10, lock);
            }
        });
        
        Thread withdrawThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                lockWithdraw(10, lock);
            }
        });
        
        withdrawThread1.start();
        withdrawThread2.start();
        
        withdrawThread1.join();
        withdrawThread2.join();
        
        System.out.println("期望余额: " + (1000 - 2000));
        System.out.println("实际余额: " + balance);
        System.out.println("结果正确: " + (balance == (1000 - 2000)) + "\n");
    }
    
    /**
     * 不安全的取款方法 - 存在线程安全问题
     */
    private static void withdraw(int amount) {
        if (balance >= amount) {
            // 模拟网络延迟
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            balance -= amount;
        }
    }
    
    /**
     * 使用synchronized关键字的线程安全取款方法
     */
    private static synchronized void synchronizedWithdraw(int amount) {
        if (balance >= amount) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            balance -= amount;
        }
    }
    
    /**
     * 使用Lock接口的线程安全取款方法
     */
    private static void lockWithdraw(int amount, java.util.concurrent.locks.Lock lock) {
        lock.lock();
        try {
            if (balance >= amount) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                balance -= amount;
            }
        } finally {
            lock.unlock(); // 确保锁被释放
        }
    }
}