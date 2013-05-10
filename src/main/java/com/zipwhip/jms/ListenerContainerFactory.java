package com.zipwhip.jms;

import com.zipwhip.util.ObjectCallback;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 4/19/11
 * Time: 8:28 PM
 * <p/>
 * Creates listeners for JMS
 */
public interface ListenerContainerFactory {

    AbstractMessageListenerContainer create(String destinationName);

    AbstractMessageListenerContainer create(String destinationName, ObjectCallback callback);

}
