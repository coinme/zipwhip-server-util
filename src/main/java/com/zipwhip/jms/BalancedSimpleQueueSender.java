package com.zipwhip.jms;

import com.zipwhip.util.SelectionStrategy;

import java.util.Map;

/**
 * @author Michael
 * @date 1/1/2015
 */
public class BalancedSimpleQueueSender implements SimpleQueueSender {

    private SelectionStrategy<SimpleQueueSender> selectionStrategy;

    @Override
    public void sendQueueJMSMessage(String queueName, String message) {
        selectionStrategy.select().sendQueueJMSMessage(queueName, message);
    }

    @Override
    public void sendQueueJMSMessage(String queueName, Map message) {
        selectionStrategy.select().sendQueueJMSMessage(queueName, message);
    }

    @Override
    public void sendQueueJMSMessage(String queueName, Object message) {
        selectionStrategy.select().sendQueueJMSMessage(queueName, message);
    }

    public SelectionStrategy<SimpleQueueSender> getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy<SimpleQueueSender> selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }
}
