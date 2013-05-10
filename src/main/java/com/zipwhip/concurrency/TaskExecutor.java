package com.zipwhip.concurrency;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskExecutor implements Executor {

    private final BlockingQueue<Runnable> queue;
    private final AbstractExecutionThreadService service = new AbstractExecutionThreadService() {
        @Override
        protected void run() throws Exception {
            while (isRunning()) {
                Runnable runnable = queue.take();
                if (runnable == null) {
                    continue;
                }
                runnable.run();
            }
        }
    };

    public TaskExecutor(BlockingQueue<Runnable> queue) {
        this.queue = queue;

        service.start();
    }

    public TaskExecutor() {
        this(new LinkedBlockingQueue<Runnable>());
    }

    public void execute(Runnable runnable) {
        this.queue.add(runnable);
    }

}