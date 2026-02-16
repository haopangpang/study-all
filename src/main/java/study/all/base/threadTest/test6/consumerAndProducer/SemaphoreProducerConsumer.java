package study.all.base.threadTest.test6.consumerAndProducer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * 基于信号量(Semaphore)的生产者-消费者实现
 * 使用Java的Semaphore类来实现经典的PV操作
 */
public class SemaphoreProducerConsumer {
    
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;
    
    // 信号量定义
    private final Semaphore empty;      // 表示空槽位数量
    private final Semaphore full;       // 表示已填充槽位数量
    private final Semaphore mutex;      // 互斥访问缓冲区
    
    public SemaphoreProducerConsumer() {
        this.empty = new Semaphore(capacity);  // 初始时所有槽位都为空
        this.full = new Semaphore(0);          // 初始时没有已填充的槽位
        this.mutex = new Semaphore(1);         // 互斥锁，初始可用
    }
    
    public static void main(String[] args) {
        System.out.println("=== 信号量(Semaphore)实现的生产者-消费者 ===\n");
        
        SemaphoreProducerConsumer semaphorePC = new SemaphoreProducerConsumer();
        
        // 创建生产者和消费者线程
        Thread producer1 = new Thread(semaphorePC.new Producer(1), "生产者-A");
        Thread producer2 = new Thread(semaphorePC.new Producer(2), "生产者-B");
        Thread consumer1 = new Thread(semaphorePC.new Consumer(1), "消费者-X");
        Thread consumer2 = new Thread(semaphorePC.new Consumer(2), "消费者-Y");
        
        // 启动所有线程
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        
        // 运行一段时间后停止
        try {
            Thread.sleep(15000);
            System.out.println("\n--- 停止所有线程 ---");
            producer1.interrupt();
            producer2.interrupt();
            consumer1.interrupt();
            consumer2.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 信号量实现的生产方法
     * P(empty) - 等待空槽位
     * P(mutex) - 获取互斥锁
     * 生产产品
     * V(mutex) - 释放互斥锁
     * V(full)  - 增加已填充槽位
     */
    private void produce(int producerId, int item) throws InterruptedException {
        // P操作：等待空槽位
        empty.acquire();  // 等待有空位置
        
        // P操作：获取互斥访问权限
        mutex.acquire();
        try {
            // 临界区：生产产品
            buffer.offer(item);
            System.out.println("生产者-" + producerId + " 生产了产品: " + item + 
                              ", 缓冲区大小: " + buffer.size());
        } finally {
            // V操作：释放互斥锁
            mutex.release();
        }
        
        // V操作：增加已填充槽位数量
        full.release();
    }
    
    /**
     * 信号量实现的消费方法
     * P(full)  - 等待已填充槽位
     * P(mutex) - 获取互斥锁
     * 消费产品
     * V(mutex) - 释放互斥锁
     * V(empty) - 增加空槽位数量
     */
    private int consume(int consumerId) throws InterruptedException {
        // P操作：等待有产品可消费
        full.acquire();   // 等待有产品
        
        // P操作：获取互斥访问权限
        mutex.acquire();
        try {
            // 临界区：消费产品
            int item = buffer.poll();
            System.out.println("\t\t\t消费者-" + consumerId + " 消费了产品: " + item + 
                              ", 缓冲区大小: " + buffer.size());
            return item;
        } finally {
            // V操作：释放互斥锁
            mutex.release();
            // V操作：增加空槽位数量
            empty.release();
        }
    }
    
    /**
     * 生产者线程类
     */
    class Producer implements Runnable {
        private int producerId;
        private int itemCount = 0;
        
        public Producer(int producerId) {
            this.producerId = producerId;
        }
        
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int item = ++itemCount;
                    produce(producerId, item);
                    Thread.sleep((long) (Math.random() * 1000) + 500); // 随机生产间隔
                }
            } catch (InterruptedException e) {
                System.out.println("生产者-" + producerId + " 被中断");
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 消费者线程类
     */
    class Consumer implements Runnable {
        private int consumerId;
        
        public Consumer(int consumerId) {
            this.consumerId = consumerId;
        }
        
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    consume(consumerId);
                    Thread.sleep((long) (Math.random() * 1500) + 800); // 随机消费间隔
                }
            } catch (InterruptedException e) {
                System.out.println("消费者-" + consumerId + " 被中断");
                Thread.currentThread().interrupt();
            }
        }
    }
}