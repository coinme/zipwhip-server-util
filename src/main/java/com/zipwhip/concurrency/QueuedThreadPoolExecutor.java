package com.zipwhip.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 4/19/11
 * Time: 3:32 PM
 * <p/>
 * http://download.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 */
public class QueuedThreadPoolExecutor {

    public static final int DEFAULT_CORE_POOL_SIZE = 5;
    public static final int DEFAULT_MAX_POOL_SIZE = 10;
    public static final int DEFAULT_QUEUE_SIZE = 20;
    public static final long DEFAULT_THREAD_KEEP_ALIVE_SECONDS = 1;

    private static final RejectedExecutionHandler BLOCKING_POLICY = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * A default configuration of ThreadPoolExecutor
     *
     * @param callback rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) will be called if Threads are exhausted and Queue is full
     * @return a configured QueuedThreadPoolExecutor
     */
    public static ThreadPoolExecutor newDefaultQueuedThreadPoolExecutor(RejectedExecutionHandler callback) {

        return new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE,             // The number of threads to keep in the pool, even if they are idle
                DEFAULT_MAX_POOL_SIZE,              // The maximum number of threads to allow in the pool
                DEFAULT_THREAD_KEEP_ALIVE_SECONDS,  // When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
                TimeUnit.SECONDS,                   // The time unit for the keepAliveTime argument
                new LinkedBlockingQueue<Runnable>(DEFAULT_QUEUE_SIZE),
                callback);
    }

    public static Executor newDefaultQueuedThreadPoolExecutor(String name, int size, int capacity) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(size, size, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(capacity),
                new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
                BLOCKING_POLICY);

        executor.allowCoreThreadTimeOut(false);

        return executor;
    }

    // TODO: Provide other configurations which are tuned to particular execution environments

}
