package com.zipwhip.jms;

import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Mar 15, 2010
 * Time: 7:40:08 PM
 */
public class JmsTemplateSimpleQueueSender implements SimpleQueueSender {

    JmsTemplate jmsTemplate;

    public JmsTemplateSimpleQueueSender() {
    }

    public JmsTemplateSimpleQueueSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    // give us something we're comfortable with
    public void sendQueueJMSMessage(String queueName, String message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    public void sendQueueJMSMessage(String queueName, Map message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    public void sendQueueJMSMessage(String queueName, Object message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
