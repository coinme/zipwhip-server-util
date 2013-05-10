package com.zipwhip.jms;

import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.StringUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 4/19/11
 * Time: 8:21 PM
 * <p/>
 * A complex object that solves a special problem that I had with consuming messages
 */
public class ListenerContainerManager implements InitializingBean {

    List<Map<String, List<Map<String, Object>>>> routes;

    ListenerContainerFactory listenerContainerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map<String, List<Map<String, Object>>> route : routes) {
            for (String uri : route.keySet()) {
                if (StringUtil.isNullOrEmpty(uri)) {
                    continue;
                }

                List<Map<String, Object>> mappings = route.get(uri);
                for (Map<String, Object> mapping : mappings) {
                    Object bean = mapping.get("bean");
                    if (bean == null) {
                        continue;
                    }
                    String method = CollectionUtil.getString(mapping, "method");
                    if (StringUtil.isNullOrEmpty(method)) {
                        continue;
                    }

                    MessageListenerAdapter adapter = new MessageListenerAdapter(bean);

                    adapter.setDefaultListenerMethod(method);

                    AbstractMessageListenerContainer container = listenerContainerFactory.create(uri);

                    container.setDestinationName(uri);

                    container.setMessageListener(adapter);

                    container.afterPropertiesSet();

                    container.start();

                    System.out.println(">> Listener created for " + uri + " wired to " + container);
                }
            }
        }
    }

    public List<Map<String, List<Map<String, Object>>>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Map<String, List<Map<String, Object>>>> routes) {
        this.routes = routes;
    }

    public ListenerContainerFactory getListenerContainerFactory() {
        return listenerContainerFactory;
    }

    public void setListenerContainerFactory(ListenerContainerFactory listenerContainerFactory) {
        this.listenerContainerFactory = listenerContainerFactory;
    }

}

