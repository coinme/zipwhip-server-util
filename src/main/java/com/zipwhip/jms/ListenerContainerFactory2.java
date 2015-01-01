package com.zipwhip.jms;

import com.zipwhip.lifecycle.CascadingDestroyable;
import com.zipwhip.lifecycle.Destroyable;
import com.zipwhip.util.ObjectCallback;

/**
 * @author Michael
 * @date 1/1/2015
 *
 * This class will create JMS consumers for you. When you destroy the destroyable, it will tear down the JMS consumer.
 * I created this 2nd version of ListenerContainerFactory so I could have multiple consumers underlying one create call.
 */
public interface ListenerContainerFactory2 {

    /**
     * When you destroy this destroyable, it will tear down the consumer.
     *
     * @param queueName
     * @param callback
     * @return
     */
    JmsLifecycleContainer create(String queueName, ObjectCallback callback);

}
