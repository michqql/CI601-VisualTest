package me.mp1282.visualtest.system.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {

    private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public static <T extends IEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.compute(eventType, (k, list) -> {
            if(list == null) list = new ArrayList<>();

            list.add(listener);
            return list;
        });
    }

    public static <T extends IEvent> void publish(T event) {
        List<Consumer<?>> list = listeners.get(event.getClass());
        if(list != null) {
            for(Consumer<?> listener : list) {
                //noinspection unchecked
                ((Consumer<T>) listener).accept(event);
            }
        }
    }

    private EventBus() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate EventBus class.");
    }
}
