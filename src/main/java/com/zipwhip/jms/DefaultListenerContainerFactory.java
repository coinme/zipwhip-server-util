package com.zipwhip.jms;

import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.ObjectCallback;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 4/19/11
 * Time: 8:29 PM
 */
public class DefaultListenerContainerFactory implements ListenerContainerFactory {

    private ConnectionFactory connectionFactory;
    private int concurrentConsumers = 1;
    private int defaultMaxConcurrentConsumers = 1;
    //    int idleConsumerLimit = 1;
    private int idleTaskExecutionLimit = 1;
    private int maxMessagesPerTask = -1;
    private int receiveTimeout = 1000;
    private int cacheLevel = DefaultMessageListenerContainer.CACHE_CONSUMER;
    private PlatformTransactionManager transactionManager;

    private Map<String, Integer> maxConcurrentConsumers;

    @Override
    public AbstractMessageListenerContainer create(String destinationName) {
        DefaultMessageListenerContainer result = new DefaultMessageListenerContainer();

        if (CollectionUtil.exists(maxConcurrentConsumers)) {
            result.setMaxConcurrentConsumers(CollectionUtil.getInteger(maxConcurrentConsumers, destinationName, defaultMaxConcurrentConsumers));
        } else {
            result.setMaxConcurrentConsumers(defaultMaxConcurrentConsumers);
        }

        result.setIdleTaskExecutionLimit(idleTaskExecutionLimit);
        result.setMaxMessagesPerTask(maxMessagesPerTask);
        result.setReceiveTimeout(receiveTimeout);
        result.setCacheLevel(cacheLevel);
        result.setDestinationName(destinationName);
        result.setAutoStartup(false);
        result.setConcurrentConsumers(concurrentConsumers);
//        result.setIdleConsumerLimit(idleConsumerLimit);

        result.setPubSubDomain(false);
        result.setConnectionFactory(connectionFactory);
        // so it will show up nice in the JProfiler
        result.setBeanName(destinationName);
        result.setTransactionManager(transactionManager);

        if (transactionManager != null) {
            result.setSessionTransacted(true);
        }

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

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

//    public int getIdleConsumerLimit() {
//        return idleConsumerLimit;
//    }
//
//    public void setIdleConsumerLimit(int idleConsumerLimit) {
//        this.idleConsumerLimit = idleConsumerLimit;
//    }

    public Map<String, Integer> getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(Map<String, Integer> maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getDefaultMaxConcurrentConsumers() {
        return defaultMaxConcurrentConsumers;
    }

    public void setDefaultMaxConcurrentConsumers(int defaultMaxConcurrentConsumers) {
        this.defaultMaxConcurrentConsumers = defaultMaxConcurrentConsumers;
    }

    public int getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(int cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public int getIdleTaskExecutionLimit() {
        return idleTaskExecutionLimit;
    }

    public void setIdleTaskExecutionLimit(int idleTaskExecutionLimit) {
        this.idleTaskExecutionLimit = idleTaskExecutionLimit;
    }

    public int getMaxMessagesPerTask() {
        return maxMessagesPerTask;
    }

    public void setMaxMessagesPerTask(int maxMessagesPerTask) {
        this.maxMessagesPerTask = maxMessagesPerTask;
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
}
