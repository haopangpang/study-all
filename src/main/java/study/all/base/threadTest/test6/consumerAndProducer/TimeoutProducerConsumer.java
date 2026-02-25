package study.all.base.threadTest.test6.consumerAndProducer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于超时机制的生产者-消费者实现
 * 演示如何处理超时和优雅关闭
 */
public class TimeoutProducerConsumer {
    
    private final BlockingQueue<Integer> buffer;
    private final int capacity = 5;
    private volatile boolean shutdown = false; // 优雅关闭标志
    
    public TimeoutProducerConsumer() {
        this.buffer = new ArrayBlockingQueue<>(capacity);
    }
    
    public static void main(String[] args) {
        System.out.println("=== 超时机制实现的生产者-消费者 ===\n");
        
        TimeoutProducerConsumer timeoutPC = new TimeoutProducerConsumer();
        
        Thread producer = new Thread(timeoutPC.new Producer(), "超时生产者");
        Thread consumer = new Thread(timeoutPC.new Consumer(), "超时消费者");
        
        producer.start();
        consumer.start();
        
        // 运行一段时间后优雅关闭
        try {
            Thread.sleep(10000);
            System.out.println("\n--- 开始优雅关闭 ---");
            timeoutPC.shutdown();
            
            // 等待线程完成
            producer.join(2000);
            consumer.join(2000);
            
            if (producer.isAlive() || consumer.isAlive()) {
                System.out.println("强制终止剩余线程");
                producer.interrupt();
                consumer.interrupt();
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void shutdown() {
        this.shutdown = true;
        System.out.println("发送关闭信号...");
    }
    
    /**
     * 带超时的生产方法
     */
    private boolean produceWithTimeout(int item, long timeout, TimeUnit unit) throws InterruptedException {
        // offer()方法支持超时，不会无限阻塞
        boolean success = buffer.offer(item, timeout, unit);
        if (success) {
            System.out.println("生产者生产了产品: " + item + ", 缓冲区大小: " + buffer.size());
        } else {
            System.out.println("生产超时，放弃生产产品: " + item);
        }
        return success;
    }
    
    /**
     * 带超时的消费方法
     */
    private Integer consumeWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        // poll()方法支持超时，不会无限阻塞
        Integer item = buffer.poll(timeout, unit);
        if (item != null) {
            System.out.println("\t\t\t消费者消费了产品: " + item + ", 缓冲区大小: " + buffer.size());
        } else {
            System.out.println("\t\t\t消费超时，缓冲区为空");
        }
        return item;
    }
    
    /**
     * 生产者线程类 - 支持优雅关闭
     */
    class Producer implements Runnable {
        private int itemCount = 0;
        
        @Override
        public void run() {
            System.out.println("生产者开始运行...");
            try {
                while (!shutdown && !Thread.currentThread().isInterrupted()) {
                    int item = ++itemCount;
                    
                    // 尝试生产，超时时间为2秒
                    if (!produceWithTimeout(item, 2, TimeUnit.SECONDS)) {
                        // 生产失败，可能是缓冲区满或超时
                        if (shutdown) break;
                        Thread.sleep(1000); // 短暂休息后重试
                        continue;
                    }
                    
                    Thread.sleep(800); // 生产间隔
                    
                }
            } catch (InterruptedException e) {
                System.out.println("生产者被中断");
            } finally {
                System.out.println("生产者线程结束");
            }
        }
    }
    
    /**
     * 消费者线程类 - 支持优雅关闭
     */
    class Consumer implements Runnable {
        @Override
        public void run() {
            System.out.println("消费者开始运行...");
            try {
                while (!shutdown && !Thread.currentThread().isInterrupted()) {
                    // 尝试消费，超时时间为3秒
                    Integer item = consumeWithTimeout(3, TimeUnit.SECONDS);
                    
                    if (item == null) {
                        // 消费失败，可能是缓冲区空或超时
                        if (shutdown) break;
                        continue;
                    }
                    
                    Thread.sleep(1200); // 消费间隔
                    
                }
            } catch (InterruptedException e) {
                System.out.println("消费者被中断");
            } finally {
                System.out.println("消费者线程结束");
            }
        }
    }
}