package com.zipwhip.jms;

import com.zipwhip.lifecycle.CascadingDestroyable;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.lifecycle.Destroyable;
import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.ObjectCallback;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Michael
 * @date 1/1/2015
 *
 * This is a special case implementation. It allows you to host multiple underlying ListenerContainerFactories under 1
 * destroyable.
 *
 * We're merging the old solution and new solution for class simplicity (reduce the number of classes). Even though
 * this might (at first glance) be considered architecturally messy.
 *
 * The rest of our projects already use ListenerContainerFactory(1) so I'm going to integrate with that
 * (hence the name 'Adapted')
 */
public class AdaptedBalancedDefaultListenerContainerFactory2 implements ListenerContainerFactory2 {

    private static final Logger LOGGER = getLogger(AdaptedBalancedDefaultListenerContainerFactory2.class);

    private Collection<ListenerContainerFactory> factories;

    @Override
    public JmsLifecycleContainer create(final String destinationName, final ObjectCallback callback) {
        if (CollectionUtil.isNullOrEmpty(factories)) {
            return null;
        }

        final ArrayList<JmsLifecycleContainer> list = new ArrayList<JmsLifecycleContainer>(factories.size());

        for (final ListenerContainerFactory factory : factories) {
            // When the parent object (MultipleJmsLifecycleContainer) is destroyed, cascade that down to "destroyable (SingleJmsLifecycleContainerAdapter)"
            list.add(new SingleJmsLifecycleContainerAdapter(factory.create(destinationName, callback)));
        }

        return new MultipleJmsLifecycleContainer(list);
    }

    public Collection<ListenerContainerFactory> getFactories() {
        return factories;
    }

    public void setFactories(Collection<ListenerContainerFactory> factories) {
        this.factories = factories;
    }
}
