# Java线程API方法详细对比

## 四种核心方法概述

### 1. Thread.sleep(long millis)
**作用**：让当前线程暂停执行指定毫秒数

**特点**：
- 静态方法，只能作用于当前线程
- **不释放任何锁资源**
- 线程进入**TIMED_WAITING**状态
- 可以被中断唤醒

### 2. Object.wait()
**作用**：让当前线程等待并释放对象监视器锁

**特点**：
- 必须在synchronized块中调用
- **释放调用对象的锁**
- 线程进入**WAITING**状态
- 需要其他线程调用notify()/notifyAll()唤醒

### 3. Thread.join()
**作用**：让当前线程等待目标线程执行完成

**特点**：
- 实例方法，作用于目标线程
- **不涉及锁的释放**
- 调用线程进入**WAITING**或**TIMED_WAITING**状态
- 目标线程结束后自动唤醒

### 4. Thread.yield()
**作用**：让当前线程主动让出CPU执行权

**特点**：
- 静态方法，只能作用于当前线程
- **不释放任何锁资源**
- 线程仍然处于**RUNNABLE**状态
- 只是建议JVM调度器重新考虑线程执行顺序

## 详细对比表格

| 特性 | sleep() | wait() | join() | yield() |
|------|---------|--------|--------|---------|
| **方法类型** | 静态方法 | 实例方法 | 实例方法 | 静态方法 |
| **作用线程** | 当前线程 | 当前线程 | 调用线程等待目标线程 | 当前线程 |
| **锁释放** | 不释放 | 释放对象锁 | 不释放 | 不释放 |
| **状态变化** | TIMED_WAITING | WAITING | WAITING/TIMED_WAITING | RUNNABLE |
| **唤醒方式** | 超时自动 | notify/notifyAll | 目标线程结束 | 调度器决定 |
| **中断响应** | 可中断 | 可中断 | 可中断 | 不适用 |
| **使用位置** | 任意位置 | synchronized块内 | 任意位置 | 任意位置 |
| **主要用途** | 时间延迟 | 线程间协作 | 执行顺序控制 | CPU资源共享 |

## 使用场景分析

### sleep() 适用场景
```java
// 1. 轮询间隔控制
while(!condition) {
    Thread.sleep(1000); // 每秒检查一次
}

// 2. 模拟网络延迟
public void simulateNetworkDelay() {
    try {
        Thread.sleep(2000); // 模拟2秒网络延迟
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

// 3. 定时任务执行
scheduler.scheduleAtFixedRate(() -> {
    Thread.sleep(5000); // 5秒执行间隔
}, 0, 10, TimeUnit.SECONDS);
```

### wait()/notify() 适用场景
```java
// 生产者-消费者模式
class Buffer {
    private final Queue<String> queue = new LinkedList<>();
    private final int capacity = 10;
    
    public synchronized void put(String item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait(); // 缓冲区满时等待
        }
        queue.offer(item);
        notifyAll(); // 通知消费者
    }
    
    public synchronized String take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // 缓冲区空时等待
        }
        String item = queue.poll();
        notifyAll(); // 通知生产者
        return item;
    }
}
```

### join() 适用场景
```java
// 1. 依赖任务执行
Thread dataLoader = new Thread(this::loadData);
Thread dataProcessor = new Thread(this::processData);

dataLoader.start();
dataLoader.join(); // 等待数据加载完成
dataProcessor.start();

// 2. 并行任务汇总
List<Thread> workers = createWorkerThreads();
workers.forEach(Thread::start);

// 等待所有工作完成
for (Thread worker : workers) {
    worker.join();
}
System.out.println("所有工作完成");

// 3. 资源初始化顺序
Thread dbConnection = new Thread(this::initDatabase);
Thread cacheInit = new Thread(this::initCache);

dbConnection.start();
dbConnection.join(); // 数据库必须先初始化完成
cacheInit.start();
```

### yield() 适用场景
```java
// 1. CPU密集型任务间的礼让
class ComputationTask implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            // 执行计算
            if (i % 10000 == 0) {
                Thread.yield(); // 让出CPU给其他任务
            }
        }
    }
}

// 2. 实时性要求不高的后台任务
class BackgroundTask implements Runnable {
    @Override
    public void run() {
        while (running) {
            doBackgroundWork();
            Thread.yield(); // 给前台任务更多CPU时间
        }
    }
}
```

## 关键区别详解

### 1. 锁处理差异
```java
// sleep() - 不释放锁
synchronized(lock) {
    Thread.sleep(1000); // 锁仍然被持有
    // 其他线程无法获得lock锁
}

// wait() - 释放锁
synchronized(lock) {
    lock.wait(); // 自动释放lock锁
    // 其他线程可以获得lock锁
}

// join() - 不涉及锁
thread.join(); // 不影响任何锁状态

// yield() - 不释放锁
synchronized(lock) {
    Thread.yield(); // 锁仍然被持有
    // 只是让出CPU执行权
}
```

### 2. 状态转换差异
```java
// sleep()状态转换
RUNNABLE → TIMED_WAITING → RUNNABLE

// wait()状态转换  
RUNNABLE → WAITING → RUNNABLE (被notify)

// join()状态转换
RUNNABLE → WAITING/TIMED_WAITING → RUNNABLE

// yield()状态转换
RUNNABLE → RUNNABLE (仍在可运行状态)
```

### 3. 唤醒机制差异
```java
// sleep() - 自动唤醒
Thread.sleep(1000); // 1秒后自动恢复

// wait() - 需要外部唤醒
obj.wait(); // 需要obj.notify()唤醒

// join() - 目标线程结束自动唤醒
thread.join(); // thread结束后自动恢复

// yield() - 调度器决定
Thread.yield(); // 何时恢复由JVM调度器决定
```

## 常见误区和注意事项

### 1. wait()必须在synchronized中使用
```java
// ❌ 错误用法
obj.wait(); // throws IllegalMonitorStateException

// ✅ 正确用法
synchronized(obj) {
    obj.wait();
}
```

### 2. sleep()可以被中断
```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // 必须处理中断
    Thread.currentThread().interrupt(); // 恢复中断状态
}
```

### 3. yield()效果不确定
```java
// yield()只是建议，不是保证
Thread.yield(); // JVM可能忽略这个建议
// 在某些JVM实现中效果可能不明显
```

### 4. join()的超时使用
```java
// 推荐使用带超时的join
if (!thread.join(5000)) {
    // 5秒后仍未完成的处理
    System.out.println("线程执行超时");
}
```

## 性能考虑

### 1. CPU使用率
- **sleep()**: 低CPU使用，适合定时任务
- **wait()**: 低CPU使用，适合事件驱动
- **join()**: 低CPU使用，适合顺序控制
- **yield()**: 中等CPU使用，适合协作式调度

### 2. 响应时间
- **sleep()**: 响应时间=睡眠时间
- **wait()**: 响应时间取决于notify时机
- **join()**: 响应时间取决于目标线程执行时间
- **yield()**: 响应时间最短但不确定

### 3. 资源消耗
- **sleep()/yield()**: 资源消耗最小
- **wait()**: 需要额外的对象监视器
- **join()**: 需要线程间的状态监控

## 最佳实践建议

1. **选择合适的同步机制**
   - 简单延时用sleep()
   - 线程协作用wait()/notify()
   - 执行顺序控制用join()
   - CPU资源共享用yield()

2. **正确处理中断**
   ```java
   try {
       Thread.sleep(1000);
   } catch (InterruptedException e) {
       Thread.currentThread().interrupt(); // 恢复中断状态
       // 进行清理工作
   }
   ```

3. **避免忙等待**
   ```java
   // ❌ 避免这种写法
   while (!condition) {
       // 空循环浪费CPU
   }
   
   // ✅ 使用wait()或sleep()
   synchronized (lock) {
       while (!condition) {
           lock.wait();
       }
   }
   ```

4. **合理使用超时**
   ```java
   // 为所有等待操作设置合理的超时时间
   thread.join(30000); // 最多等待30秒
   obj.wait(5000);     // 最多等待5秒
   ```

理解这些方法的本质区别对于编写高效的并发程序至关重要。