package com.zipwhip.jms;

import com.google.common.base.Preconditions;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.util.CollectionUtil;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Michael
 * @date 1/1/2015
 */
public class MultipleJmsLifecycleContainer extends CascadingDestroyableBase implements JmsLifecycleContainer {

    private final Collection<JmsLifecycleContainer> containers;

    public MultipleJmsLifecycleContainer(Collection<JmsLifecycleContainer> containers) {
        Preconditions.checkNotNull(containers);

        this.containers = Collections.unmodifiableCollection(containers);

        for (JmsLifecycleContainer container : this.containers) {
            this.link(container);
        }
    }

    @Override
    public boolean isRunning() {
        for (JmsLifecycleContainer container : containers) {
            // if ANY are running, they are all running.
            if (container.isRunning()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAutoStartup() {
        for (JmsLifecycleContainer container : containers) {
            // if ANY of them are false, they are all false.
            if (!container.isAutoStartup()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void start() {
        for (JmsLifecycleContainer container : containers) {
            container.start();
        }
    }

    @Override
    protected void onDestroy() {
        // linking handles this.
    }
}
