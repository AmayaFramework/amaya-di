package io.github.amayaframework;

import io.github.amayaframework.di.containers.ContainerProvider;
import io.github.amayaframework.di.containers.LockProvider;
import io.github.amayaframework.di.containers.MapContainer;
import io.github.amayaframework.di.containers.Value;

public final class Values {
    // Container
    private static final Object LOCK = new Object();
    private static final MapContainer CONTAINER = new MapContainer();
    // io.github.amayaframework.Values
    private static final Value<Integer> FIELD1 = new Value<>("FIELD1", Integer.class);
    private static final Value<Integer> FIELD2 = new Value<>("FIELD2", Integer.class);
    private static final Value<Integer> FIELD3 = new Value<>("FIELD3", Integer.class);
    private static final Value<Integer> FIELD4 = new Value<>("FIELD4", Integer.class);

    static {
        CONTAINER.putValue(FIELD1, 1);
        CONTAINER.putValue(FIELD2, 2);
        CONTAINER.putValue(FIELD3, 3);
        CONTAINER.putValue(FIELD4, 4);
    }

    @ContainerProvider
    public static MapContainer getContainer() {
        return CONTAINER;
    }

    @LockProvider
    public static Object getLock() {
        return LOCK;
    }
}
