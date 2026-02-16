package study.all.base.threadTest.test6.consumerAndProducer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 基于管程(Monitor)的生产者-消费者实现
 * 管程是Java中synchronized关键字的理论基础
 */
public class MonitorProducerConsumer {
    
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;
    
    public static void main(String[] args) {
        System.out.println("=== 管程(Monitor)实现的生产者-消费者 ===\n");
        
        MonitorProducerConsumer monitorPC = new MonitorProducerConsumer();
        
        // 创建生产者和消费者线程
        Thread producer1 = new Thread(monitorPC.new Producer(1), "生产者-1");
        Thread producer2 = new Thread(monitorPC.new Producer(2), "生产者-2");
        Thread consumer1 = new Thread(monitorPC.new Consumer(1), "消费者-1");
        Thread consumer2 = new Thread(monitorPC.new Consumer(2), "消费者-2");
        
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
     * 管程中的生产方法 - 使用synchronized关键字
     */
    private synchronized void produce(int producerId, int item) throws InterruptedException {
        // 等待缓冲区有空间
        while (buffer.size() == capacity) {
            System.out.println("生产者-" + producerId + " 发现缓冲区已满，等待...");
            wait(); // 释放锁并等待
        }
        
        // 生产产品
        buffer.offer(item);
        System.out.println("生产者-" + producerId + " 生产了产品: " + item + 
                          ", 缓冲区大小: " + buffer.size());
        
        // 通知等待的消费者
        notifyAll();
    }
    
    /**
     * 管程中的消费方法 - 使用synchronized关键字
     */
    private synchronized int consume(int consumerId) throws InterruptedException {
        // 等待缓冲区有产品
        while (buffer.isEmpty()) {
            System.out.println("\t\t\t消费者-" + consumerId + " 发现缓冲区为空，等待...");
            wait(); // 释放锁并等待
        }
        
        // 消费产品
        int item = buffer.poll();
        System.out.println("\t\t\t消费者-" + consumerId + " 消费了产品: " + item + 
                          ", 缓冲区大小: " + buffer.size());
        
        // 通知等待的生产者
        notifyAll();
        return item;
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