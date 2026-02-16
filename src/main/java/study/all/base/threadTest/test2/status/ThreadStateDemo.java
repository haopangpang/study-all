package study.all.base.threadTest.test2.status;

/**
 * Java线程状态演示类
 * 展示各种线程状态及其转换过程
 */
public class ThreadStateDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java线程状态演示 ===\n");
        
        // 1. NEW状态演示
        demonstrateNewState();
        
        // 2. RUNNABLE状态演示
        demonstrateRunnableState();
        
        // 3. BLOCKED状态演示
        demonstrateBlockedState();
        
        // 4. WAITING状态演示
        demonstrateWaitingState();
        
        // 5. TIMED_WAITING状态演示
        demonstrateTimedWaitingState();
        
        // 6. TERMINATED状态演示
        demonstrateTerminatedState();
    }
    
    /**
     * 演示NEW状态
     */
    private static void demonstrateNewState() {
        System.out.println("1. NEW状态演示:");
        Thread newThread = new Thread(() -> {
            System.out.println("线程开始执行");
        });
        System.out.println("线程创建后状态: " + newThread.getState()); // NEW
        System.out.println();
    }
    
    /**
     * 演示RUNNABLE状态
     */
    private static void demonstrateRunnableState() throws InterruptedException {
        System.out.println("2. RUNNABLE状态演示:");
        Thread runnableThread = new Thread(() -> {
            // 线程执行中的工作
            for (int i = 0; i < 1000000; i++) {
                Math.sqrt(i); // CPU密集型操作
            }
            System.out.println("RUNNABLE线程执行完成");
        });
        
        runnableThread.start();
        Thread.sleep(10); // 给线程一些执行时间
        System.out.println("线程启动后状态: " + runnableThread.getState()); // RUNNABLE
        runnableThread.join(); // 等待线程执行完毕
        System.out.println();
    }
    
    /**
     * 演示BLOCKED状态
     */
    private static void demonstrateBlockedState() throws InterruptedException {
        System.out.println("3. BLOCKED状态演示:");
        
        final Object lock = new Object();
        
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread1获得了锁");
                try {
                    Thread.sleep(2000); // 持有锁2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread1释放了锁");
            }
        });
        
        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread2获得了锁");
            }
        });
        
        thread1.start();
        Thread.sleep(100); // 确保thread1先获得锁
        thread2.start();
        Thread.sleep(100); // 给thread2一些时间尝试获取锁
        
        System.out.println("Thread2状态: " + thread2.getState()); // BLOCKED
        thread1.join(); // 当前主线程等待thread1线程执行完毕
        thread2.join(); // 当前主线程等待thread2线程执行完毕
        System.out.println();
    }
    
    /**
     * 演示WAITING状态
     */
    private static void demonstrateWaitingState() throws InterruptedException {
        System.out.println("4. WAITING状态演示:");
        
        Object lock = new Object();
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("线程进入WAITING状态");
                    lock.wait(); // 无限期等待
                    System.out.println("线程被唤醒");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        waitingThread.start();
        Thread.sleep(100);
        System.out.println("等待线程状态: " + waitingThread.getState()); // WAITING
        
        // 唤醒线程
        synchronized (lock) {
            lock.notify();
        }
        waitingThread.join();
        System.out.println();
    }
    
    /**
     * 演示TIMED_WAITING状态
     */
    private static void demonstrateTimedWaitingState() throws InterruptedException {
        System.out.println("5. TIMED_WAITING状态演示:");
        
        Thread timedWaitingThread = new Thread(() -> {
            try {
                System.out.println("线程进入TIMED_WAITING状态");
                Thread.sleep(3000); // 睡眠3秒
                System.out.println("线程睡眠结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        timedWaitingThread.start();
        Thread.sleep(100);
        System.out.println("睡眠线程状态: " + timedWaitingThread.getState()); // TIMED_WAITING
        timedWaitingThread.join();
        System.out.println();
    }
    
    /**
     * 演示TERMINATED状态
     */
    private static void demonstrateTerminatedState() throws InterruptedException {
        System.out.println("6. TERMINATED状态演示:");
        
        Thread terminatedThread = new Thread(() -> {
            System.out.println("线程执行任务");
        });
        
        terminatedThread.start();
        terminatedThread.join(); // 等待线程执行完毕
        System.out.println("线程结束后状态: " + terminatedThread.getState()); // TERMINATED
        System.out.println();
    }
    
    /**
     * 状态转换综合演示
     */
    public static void demonstrateStateTransitions() {
        System.out.println("=== 线程状态转换综合演示 ===\n");
        
        Thread transitionThread = new Thread(() -> {
            System.out.println("线程开始执行 - RUNNABLE状态");
            
            try {
                // 模拟工作
                Thread.sleep(1000);
                System.out.println("工作完成 - 准备进入WAITING状态");
                
                // 进入WAITING状态
                synchronized (ThreadStateDemo.class) {
                    ThreadStateDemo.class.wait();
                }
                
                System.out.println("被唤醒 - 回到RUNNABLE状态");
                
                // 再次工作
                Thread.sleep(500);
                System.out.println("第二次工作完成 - 准备结束");
                
            } catch (InterruptedException e) {
                System.out.println("线程被中断");
            }
        });
        
        System.out.println("1. 创建线程后: " + transitionThread.getState()); // NEW
        
        transitionThread.start();
        System.out.println("2. 启动线程后: " + transitionThread.getState()); // RUNNABLE
        
        try {
            Thread.sleep(1500);
            System.out.println("3. 工作一段时间后: " + transitionThread.getState()); // RUNNABLE 或 TIMED_WAITING
            
            Thread.sleep(1000);
            System.out.println("4. 进入等待状态后: " + transitionThread.getState()); // WAITING
            
            // 唤醒线程
            synchronized (ThreadStateDemo.class) {
                ThreadStateDemo.class.notify();
            }
            
            Thread.sleep(100);
            System.out.println("5. 被唤醒后: " + transitionThread.getState()); // RUNNABLE
            
            transitionThread.join();
            System.out.println("6. 线程结束后: " + transitionThread.getState()); // TERMINATED
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}