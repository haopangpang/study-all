package study.all.base.threadTest.test5.tongbu;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者-消费者模式演示
 * 展示线程间协作和同步的经典案例
 */
public class ProducerConsumerDemo {
    
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    public static void main(String[] args) {
        System.out.println("=== 生产者-消费者模式演示 ===\n");
        
        ProducerConsumerDemo demo = new ProducerConsumerDemo();
        
        Thread producer = new Thread(demo.new Producer(), "生产者");
        Thread consumer = new Thread(demo.new Consumer(), "消费者");
        
        producer.start();
        consumer.start();
        
        // 运行10秒后停止
        try {
            Thread.sleep(10000);
            producer.interrupt();
            consumer.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 生产者线程
     */
    class Producer implements Runnable {
        private int productCount = 0;
        
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    lock.lock();
                    try {
                        // 如果缓冲区满了，等待消费者消费
                        while (buffer.size() == capacity) {
                            System.out.println("缓冲区已满，生产者等待...");
                            notFull.await();
                        }
                        
                        int product = ++productCount;
                        buffer.offer(product);
                        System.out.println(Thread.currentThread().getName() + 
                            " 生产了产品: " + product + ", 缓冲区大小: " + buffer.size());
                        
                        // 通知消费者可以消费了
                        notEmpty.signal();
                        
                        Thread.sleep(1000); // 模拟生产时间
                        
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("生产者被中断");
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 消费者线程
     */
    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    lock.lock();
                    try {
                        // 如果缓冲区空了，等待生产者生产
                        while (buffer.isEmpty()) {
                            System.out.println("缓冲区为空，消费者等待...");
                            notEmpty.await();
                        }
                        
                        int product = buffer.poll();
                        System.out.println("\t\t\t" + Thread.currentThread().getName() + 
                            " 消费了产品: " + product + ", 缓冲区大小: " + buffer.size());
                        
                        // 通知生产者可以继续生产了
                        notFull.signal();
                        
                        Thread.sleep(1500); // 模拟消费时间
                        
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("消费者被中断");
                Thread.currentThread().interrupt();
            }
        }
    }
}