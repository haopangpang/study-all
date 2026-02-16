package study.all.base.threadTest.test1.thread;

/**
 * Java创建线程的三种方式演示
 * 1. 继承Thread类
 * 2. 实现Runnable接口  
 * 3. 使用Callable和Future
 */
public class ThreadDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java线程创建三种方式演示 ===\n");
        
        // 方式一：继承Thread类
        demonstrateThreadClass();
        
        // 方式二：实现Runnable接口
        demonstrateRunnableInterface();
        
        // 方式三：使用Callable和Future
        demonstrateCallableInterface();
    }
    
    /**
     * 方式一：继承Thread类
     */
    private static void demonstrateThreadClass() {
        System.out.println("1. 继承Thread类方式:");
        System.out.println("-------------------");
        
        MyThread thread1 = new MyThread("Thread-1");
        MyThread thread2 = new MyThread("Thread-2");
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join(); // 等待线程执行完毕
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 方式二：实现Runnable接口
     */
    private static void demonstrateRunnableInterface() {
        System.out.println("2. 实现Runnable接口方式:");
        System.out.println("------------------------");
        
        // 创建Runnable任务
        MyRunnable task1 = new MyRunnable("Task-1");
        MyRunnable task2 = new MyRunnable("Task-2");
        
        // 将Runnable包装到Thread中
        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 方式三：使用Callable和Future
     */
    private static void demonstrateCallableInterface() {
        System.out.println("3. 使用Callable和Future方式:");
        System.out.println("---------------------------");
        
        try {
            java.util.concurrent.ExecutorService executor = 
                java.util.concurrent.Executors.newFixedThreadPool(2);
            
            MyCallable callable1 = new MyCallable("Callable-1");
            MyCallable callable2 = new MyCallable("Callable-2");
            
            // 提交Callable任务并获取Future对象
            java.util.concurrent.Future<String> future1 = executor.submit(callable1);
            java.util.concurrent.Future<String> future2 = executor.submit(callable2);
            
            // 获取执行结果（会阻塞直到任务完成）
            String result1 = future1.get();
            String result2 = future2.get();
            
            System.out.println("Callable-1 结果: " + result1);
            System.out.println("Callable-2 结果: " + result2);
            
            executor.shutdown();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
}

/**
 * 方式一：继承Thread类
 */
class MyThread extends Thread {
    private String threadName;
    
    public MyThread(String name) {
        this.threadName = name;
    }
    
    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(threadName + " 执行第 " + i + " 次");
            try {
                Thread.sleep(500); // 模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " 执行完毕");
    }
}

/**
 * 方式二：实现Runnable接口
 */
class MyRunnable implements Runnable {
    private String taskName;
    
    public MyRunnable(String name) {
        this.taskName = name;
    }
    
    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(taskName + " 处理数据块 " + i);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(taskName + " 处理完成");
    }
}

/**
 * 方式三：实现Callable接口（可以返回结果）
 */
class MyCallable implements java.util.concurrent.Callable<String> {
    private String callableName;
    
    public MyCallable(String name) {
        this.callableName = name;
    }
    
    @Override
    public String call() throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= 2; i++) {
            result.append(callableName).append("-步骤").append(i);
            if (i < 2) result.append(", ");
            Thread.sleep(400);
        }
        return result.toString();
    }
}