package com.zipwhip.jms;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Sep 18, 2010
 * Time: 4:09:00 PM
 */
public class TestSimpleQueueSender implements SimpleQueueSender {

    // give us something we're comfortable with
    public void sendQueueJMSMessage(String queueName, String message) {
        System.out.println("TEST JMS: " + queueName + ":" + message);
    }

    public void sendQueueJMSMessage(String queueName, Map message) {
        System.out.println("TEST JMS: " + queueName + ":" + message);
    }

    public void sendQueueJMSMessage(String queueName, Object message) {
        System.out.println("TEST JMS: " + queueName + ":" + message);
    }

}
