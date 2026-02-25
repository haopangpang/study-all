package study.all.base.threadTest.test6.consumerAndProducer;

/**
 * 生产者-消费者问题所有实现方式的运行测试
 * 可以逐一运行各种实现来观察效果
 */
public class AllImplementationsRunner {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("   生产者-消费者问题所有实现方式运行测试");
        System.out.println("===============================================\n");
        
        // 可以取消注释来运行不同的实现
        
        System.out.println("1. 运行管程(Monitor)实现:");
        runMonitorImplementation();
        
        System.out.println("\n2. 运行信号量(Semaphore)实现:");
        runSemaphoreImplementation();
        
        System.out.println("\n3. 运行显式锁(Lock)实现:");
        runLockImplementation();
        
        System.out.println("\n4. 运行阻塞队列(BlockingQueue)实现:");
        runBlockQueueImplementation();
        
        System.out.println("\n5. 运行超时机制实现:");
        runTimeoutImplementation();
        
        System.out.println("\n6. 查看完整比较信息:");
        showCompleteComparison();
    }
    
    private static void runMonitorImplementation() {
        System.out.println("--- 管程实现开始 ---");
        MonitorProducerConsumer.main(new String[0]);
        System.out.println("--- 管程实现结束 ---\n");
    }
    
    private static void runSemaphoreImplementation() {
        System.out.println("--- 信号量实现开始 ---");
        SemaphoreProducerConsumer.main(new String[0]);
        System.out.println("--- 信号量实现结束 ---\n");
    }
    
    private static void runLockImplementation() {
        System.out.println("--- 显式锁实现开始 ---");
        // 注意：这个在test5包中
        study.all.base.threadTest.test5.tongbu.ProducerConsumerDemo.main(new String[0]);
        System.out.println("--- 显式锁实现结束 ---\n");
    }
    
    private static void runBlockQueueImplementation() {
        System.out.println("--- 阻塞队列实现开始 ---");
        BlockQueueProducerConsumer.main(new String[0]);
        System.out.println("--- 阻塞队列实现结束 ---\n");
    }
    
    private static void runTimeoutImplementation() {
        System.out.println("--- 超时机制实现开始 ---");
        TimeoutProducerConsumer.main(new String[0]);
        System.out.println("--- 超时机制实现结束 ---\n");
    }
    
    private static void showCompleteComparison() {
        System.out.println("--- 完整比较信息 ---");
        CompleteProducerConsumerComparison.main(new String[0]);
    }
}