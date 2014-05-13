package com.zipwhip.jms;

import com.zipwhip.concurrency.QueuedThreadPoolExecutor;
import com.zipwhip.util.ObjectCallback;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import javax.jms.ConnectionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/9/11
 * Time: 5:01 PM
 */
public class SimpleListenerContainerFactory implements ListenerContainerFactory {

    private ConnectionFactory connectionFactory;
    private int concurrentConsumers = 1;
    private int queueDepth;

    @Override
    public AbstractMessageListenerContainer create(String destinationName) {
        SimpleMessageListenerContainer result = new SimpleMessageListenerContainer();

        result.setAutoStartup(false);
        result.setConcurrentConsumers(concurrentConsumers);
        result.setDestinationName(destinationName);
        result.setPubSubDomain(false);
        result.setConnectionFactory(connectionFactory);
        // so it will show up nice in the JProfiler
        result.setBeanName("Bean:" + destinationName);

        result.setTaskExecutor(QueuedThreadPoolExecutor.newDefaultQueuedThreadPoolExecutor(destinationName, concurrentConsumers, queueDepth));

        // We don't set the properties because we're missing the "MessageListener" field.
        // The caller should set that and then call "afterPropertiesSet()"
//        result.afterPropertiesSet();

        return result;
    }

    @Override
    public AbstractMessageListenerContainer create(String destinationName, ObjectCallback callback) {
        AbstractMessageListenerContainer listenerContainer = create(destinationName);

        final MessageListenerAdapter adapter = new MessageListenerAdapter(callback);

        listenerContainer.setMessageListener(adapter);

        listenerContainer.afterPropertiesSet();

        return listenerContainer;
    }


    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getQueueDepth() {
        return queueDepth;
    }

    public void setQueueDepth(int queueDepth) {
        this.queueDepth = queueDepth;
    }
}