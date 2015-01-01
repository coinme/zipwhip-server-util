package com.zipwhip.jms;

import com.zipwhip.lifecycle.CascadingDestroyable;
import com.zipwhip.lifecycle.CascadingDestroyableBase;

/**
 * @author Michael
 * @date 1/1/2015
 */
public interface JmsLifecycleContainer extends CascadingDestroyable {

    boolean isRunning();

    boolean isAutoStartup();

    void start();

}
