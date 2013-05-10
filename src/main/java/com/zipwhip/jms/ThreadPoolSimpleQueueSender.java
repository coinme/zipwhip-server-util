package com.zipwhip.jms;

import com.google.common.base.Service;
import com.google.common.collect.MapMaker;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.AbstractIdleService;
import com.zipwhip.util.RoundRobinSelectionStrategy;
import com.zipwhip.util.SelectionStrategy;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/4/12
 * Time: 5:55 PM
 * <p/>
 * Uses a thread pool of connections.
 * <p/>
 * This class has a number of problems with it.
 * <p/>
 * 1. The sender doesnt know of a problem/exception. This is a problem for retry; we just assume that it's ok. This will
 * cause us to lose tokens in production.
 * <p/>
 * 2. In the event of a broken connection, there is no retry or queueing.
 * <p/>
 * 3. There is no dead detectino; We'll throw requests against a dead connection and lose messages.
 */
public class ThreadPoolSimpleQueueSender implements SimpleQueueSender {

    private ConnectionFactory connectionFactory;

    private int concurrencyLevel = 50;
    private Connection connection;
    private final Map<String, QueueWorker> queues = new MapMaker()
            .concurrencyLevel(concurrencyLevel)
            .makeMap();

    @Override
    public void sendQueueJMSMessage(String queueName, String message) {
        sendQueueJMSMessage(queueName, (Object) message);
    }

    @Override
    public void sendQueueJMSMessage(String queueName, Map message) {
        sendQueueJMSMessage(queueName, (Object) message);
    }

    @Override
    public void sendQueueJMSMessage(String queueName, Object message) {
        QueueWorker worker = getWorker(queueName);

        try {
            worker.sendQueueJMSMessage(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private QueueWorker getWorker(String queueName) {
        QueueWorker worker = queues.get(queueName);

        if (worker == null) {
            synchronized (queues) {
                worker = queues.get(queueName);
                if (worker == null) {
                    try {
                        // this guy will round robin the requests to underlying threads
                        worker = new BalancedQueueWorker(concurrencyLevel, queueName, connectionFactory);
                        worker.start();

                        queues.put(queueName, worker);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return worker;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }

    private static interface QueueWorker extends Service {

        void sendQueueJMSMessage(Object message) throws JMSException;

    }

    private static class BalancedQueueWorker extends AbstractIdleService implements QueueWorker {

        private SelectionStrategy<QueueWorker> selectionStrategy;

        public BalancedQueueWorker(int concurrencyLevel, String queueName, ConnectionFactory connection) throws JMSException {

            List<QueueWorker> workers = new ArrayList<QueueWorker>(concurrencyLevel);
            for (int i = 0; i < concurrencyLevel; i++) {
                ThreadQueueWorker worker = new ThreadQueueWorker(queueName, connection);
                workers.add(worker);
            }

            selectionStrategy = new RoundRobinSelectionStrategy<QueueWorker>();
            selectionStrategy.setOptions(workers);
        }

        @Override
        public void sendQueueJMSMessage(Object message) throws JMSException {
            selectionStrategy.select().sendQueueJMSMessage(message);
        }

        @Override
        protected void startUp() throws Exception {
            for (QueueWorker worker : selectionStrategy.getOptions()) {
                // TODO: worry about failure
                worker.startAndWait();
            }
        }

        @Override
        protected void shutDown() throws Exception {
            for (QueueWorker worker : selectionStrategy.getOptions()) {
                worker.stopAndWait();
            }
        }
    }

    private static class ThreadQueueWorker extends AbstractExecutionThreadService implements QueueWorker {

        private static final int CAPACITY = 500;

        Session session;
        MessageProducer producer;
        MessageConverter messageConverter = new SimpleMessageConverter();
        BlockingQueue<Object> queue = new LinkedBlockingDeque<Object>(CAPACITY);
        String queueName;

        private ThreadQueueWorker(String queueName, Session session, MessageProducer producer) {
            this.session = session;
            this.producer = producer;
            this.queueName = queueName;
        }

        protected Executor executor() {
            return new Executor() {
                public void execute(Runnable command) {
                    new Thread(command, getServiceName()).start();
                }
            };
        }

        protected String getServiceName() {
            return "producer:/" + queueName;
        }

        public ThreadQueueWorker(String queueName, ConnectionFactory connectionFactory) throws JMSException {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);

            this.session = session;
            this.producer = session.createProducer(queue);
            this.queueName = queueName;
        }

        public void sendQueueJMSMessage(Object message) throws JMSException {
            try {

                if (isRunning()) {
                    // BLOCKING!
                    queue.put(message);
                } else {
                    throw new RuntimeException("Not running, cannot enqueue work.");
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//
//        @Override
//        public void start() {
//            thread = new Thread(this);
//            thread.setName("worker:/" + queueName);
//            thread.start();
//        }

//        @Override
//        public void run() {

//
//        }

        /**
         * This will stop automatically when you throw an exception
         *
         * @throws Exception
         */
        @Override
        protected void run() throws Exception {

            while (isRunning()) {
                Object message = queue.take();

                Message token = messageConverter.toMessage(message, session);

                producer.send(token);
            }

        }
    }

}
