package study.all.base.threadTest.test6.consumerAndProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于阻塞队列(BlockingQueue)的生产者-消费者实现
 * 这是最简单且推荐的方式，利用JDK内置的线程安全队列
 */
public class BlockQueueProducerConsumer {
    
    private final BlockingQueue<Integer> buffer;
    private final int capacity = 5;
    
    public BlockQueueProducerConsumer() {
        // 使用LinkedBlockingQueue作为缓冲区
        this.buffer = new LinkedBlockingQueue<>(capacity);
    }
    
    public static void main(String[] args) {
        System.out.println("=== 阻塞队列(BlockingQueue)实现的生产者-消费者 ===\n");
        
        BlockQueueProducerConsumer bqPC = new BlockQueueProducerConsumer();
        
        // 创建生产者和消费者线程
        Thread producer1 = new Thread(bqPC.new Producer(1), "生产者-P1");
        Thread producer2 = new Thread(bqPC.new Producer(2), "生产者-P2");
        Thread consumer1 = new Thread(bqPC.new Consumer(1), "消费者-C1");
        Thread consumer2 = new Thread(bqPC.new Consumer(2), "消费者-C2");
        
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
     * 使用阻塞队列的生产方法
     * put()方法会在队列满时自动阻塞
     */
    private void produce(int producerId, int item) throws InterruptedException {
        // put()方法会自动阻塞直到有空间
        buffer.put(item);
        System.out.println("生产者-" + producerId + " 生产了产品: " + item + 
                          ", 缓冲区大小: " + buffer.size());
    }
    
    /**
     * 使用阻塞队列的消费方法
     * take()方法会在队列空时自动阻塞
     */
    private int consume(int consumerId) throws InterruptedException {
        // take()方法会自动阻塞直到有元素可取
        int item = buffer.take();
        System.out.println("\t\t\t消费者-" + consumerId + " 消费了产品: " + item + 
                          ", 缓冲区大小: " + buffer.size());
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