package com.zipwhip.jms;

import com.zipwhip.lifecycle.CascadingDestroyableBase;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

/**
 * @author Michael
 * @date 1/1/2015
 */
public class SingleJmsLifecycleContainerAdapter extends CascadingDestroyableBase implements JmsLifecycleContainer {

    private final AbstractMessageListenerContainer container;

    protected SingleJmsLifecycleContainerAdapter(AbstractMessageListenerContainer container) {
        this.container = container;
    }

    @Override
    protected void onDestroy() {
        container.stop(new Runnable() {
            @Override
            public void run() {
                container.shutdown();
            }
        });
    }

    @Override
    public boolean isRunning() {
        return container.isRunning();
    }

    @Override
    public boolean isAutoStartup() {
        return container.isAutoStartup();
    }

    @Override
    public void start() {
        container.start();
    }

}
