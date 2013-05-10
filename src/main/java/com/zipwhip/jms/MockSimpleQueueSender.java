/**
 *
 */
package com.zipwhip.jms;

import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jdinsel
 */
public class MockSimpleQueueSender implements SimpleQueueSender {

	private Map<String, List<Object>> queue = new LinkedHashMap<String, List<Object>>();

	public MockSimpleQueueSender() {
	}

	public MockSimpleQueueSender(JmsTemplate jmsTemplate) {

	}

	// give us something we're comfortable with
	@Override
	public void sendQueueJMSMessage(String queueName, String message) {
		saveToken(queueName, message);
	}

	@Override
	public void sendQueueJMSMessage(String queueName, Map message) {
		saveToken(queueName, message);
	}

	@Override
	public void sendQueueJMSMessage(String queueName, Object message) {
		saveToken(queueName, message);
	}

	public JmsTemplate getJmsTemplate() {
		return null;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {

	}

	/**
	 * @param queueName
	 */
	private void saveToken(String queueName, Object... args) {
		List<Object> list = queue.get(queueName);
		if (list == null) {
			subscribe(queueName);
			list = queue.get(queueName);
		}
		if (args != null) {
			for (Object o : args) {
				list.add(o);
			}
		}
	}

	private void subscribe(String uri) {
		if (queue.containsKey(uri)) {
			return;
		} else {
			List<Object> list = new ArrayList<Object>();
			queue.put(uri, list);
		}

	}

	/**
	 * @return the queue
	 */
	public final Map<String, List<Object>> getQueue() {
		return queue;
	}

	/**
	 * @param queue
	 *            the queue to set
	 */
	public final void setQueue(Map<String, List<Object>> queue) {
		this.queue = queue;
	}



}
