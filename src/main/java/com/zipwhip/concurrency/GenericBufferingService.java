package com.zipwhip.concurrency;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.zipwhip.util.InputRunnable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/13/12
 * Time: 7:59 PM
 *
 * This is a demo implementation of the Guava ThreadService. I'm not sure the benefits of this vs the standard Java
 * Executors.newSingleThreadExecutor().
 */
public class GenericBufferingService<T> extends AbstractExecutionThreadService {

    private InputRunnable<T> runnable;
    private BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    protected boolean allowNull = false;

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            T item = queue.take();

            if (item == null && !allowNull) {
                continue;
            }

            runnable.run(item);
        }
    }

    @Override
    protected void startUp() throws Exception {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        super.startUp();
    }

    public InputRunnable<T> getRunnable() {
        return runnable;
    }

    public void setRunnable(InputRunnable<T> runnable) {
        this.runnable = runnable;
    }

    public BlockingQueue<T> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }
}
