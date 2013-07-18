package com.zipwhip.jms;

import com.zipwhip.util.Directory;
import com.zipwhip.util.ListDirectory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Sep 18, 2010
 * Time: 4:09:00 PM
 */
public class TestSimpleQueueSender implements SimpleQueueSender {

    private final Directory<String, Object> directory = new ListDirectory<String, Object>();

    // give us something we're comfortable with
    public void sendQueueJMSMessage(String queueName, String message) {
        directory.add(queueName, message);
    }

    public void sendQueueJMSMessage(String queueName, Map message) {
        directory.add(queueName, message);
    }

    public void sendQueueJMSMessage(String queueName, Object message) {
        directory.add(queueName, message);
    }

    public Directory<String, Object> getDirectory() {
        return directory;
    }
}
