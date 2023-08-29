package com.keywords.tokenizer.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author wangyue
 * @date 2023/8/15
 */
public class ThreadPoolManager {
    private ExecutorService threadPool = Executors.newFixedThreadPool(30);
    //饿汉式单例模式
    private static ThreadPoolManager instance = new ThreadPoolManager();

    // 私有
    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    public <T> Future<T> submit(Callable<T> task) {
        // 将任务提交到线程池中，并返回一个Future对象
        return threadPool.submit(task);
    }

    //还没用上
    public <T> Future<T> submit(Callable<T> task, ThreadCompleteListener listener) {
        Callable<T> wrappedTask = () -> {
            T result = task.call();
            listener.notifyOfThreadComplete();
            return result;
        };
        return threadPool.submit(wrappedTask);
    }

    public void await() {
        // 关闭线程池，不再接受新的任务
        threadPool.shutdown();
        // 循环等待直到所有任务执行结束
        while (!threadPool.isTerminated()) {
            // 空循环，不做任何操作
        }
    }
}
