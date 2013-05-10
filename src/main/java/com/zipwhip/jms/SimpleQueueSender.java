package com.zipwhip.jms;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/4/12
 * Time: 5:53 PM
 */
public interface SimpleQueueSender {

    void sendQueueJMSMessage(String queueName, String message);

    void sendQueueJMSMessage(String queueName, Map message);

    void sendQueueJMSMessage(String queueName, Object message);

}
