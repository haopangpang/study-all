# Java线程同步学习指南

## 什么是线程同步？

线程同步是指在多线程环境中，协调多个线程对共享资源的访问，防止数据竞争和不一致的状态。

## 核心概念

### 1. 竞态条件（Race Condition）
当多个线程同时访问和修改同一个共享数据时，最终的结果取决于线程执行的精确时序。

### 2. 临界区（Critical Section）
访问共享资源的代码段，需要互斥执行。

### 3. 原子性（Atomicity）
操作要么全部执行，要么全部不执行，不会被其他线程中断。

## 主要同步机制

### 1. synchronized关键字
- 方法级别同步
- 代码块级别同步
- 自动获取和释放锁

### 2. Lock接口
- 更灵活的锁定机制
- 支持公平锁、读写锁等
- 需要手动释放锁

### 3. volatile关键字
- 保证变量的可见性
- 禁止指令重排序
- 不保证原子性

### 4. 并发工具类
- CountDownLatch：倒计时门闩
- Semaphore：信号量
- CyclicBarrier：循环屏障
- Phaser：阶段器

## 实际应用场景

### 1. 银行转账系统
保护账户余额的一致性

### 2. 生产者-消费者模式
协调生产和消费过程

### 3. 缓存系统
保证缓存数据的一致性和完整性

## 最佳实践

1. 尽量减少同步范围
2. 优先使用高级并发工具
3. 避免死锁
4. 注意性能影响
5. 正确处理中断

## 运行示例

```bash
# 编译所有示例
javac src/main/java/com/example/*.java

# 运行基础同步演示
java com.example.ThreadSynchronizationDemo

# 运行生产者-消费者演示
java com.example.ProducerConsumerDemo

# 运行CountDownLatch演示
java com.example.CountDownLatchDemo
```