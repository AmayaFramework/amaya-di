package io.github.amayaframework.di;

import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ContainerProvider;
import io.github.amayaframework.di.containers.LockProvider;
import io.github.amayaframework.di.containers.MapContainer;

/**
 * A class representing the default provider. Uses a lazy singleton.
 */
public final class Provider {

    @ContainerProvider
    public static Container getContainer() {
        return Holder.CONTAINER;
    }

    @LockProvider
    public static Object getLock() {
        return Holder.LOCK;
    }

    private static class Holder {
        private static final Container CONTAINER = new MapContainer();
        private static final Object LOCK = new Object();
    }
}
