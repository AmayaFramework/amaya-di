package io.github.amayaframework.di;

import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.LockMethod;
import io.github.amayaframework.di.containers.MapContainer;
import io.github.amayaframework.di.containers.ProviderMethod;

/**
 * A class representing the default provider. Uses a lazy singleton.
 */
public final class Provider {

    @ProviderMethod
    public static Container getContainer() {
        return ContainerHolder.CONTAINER;
    }

    @LockMethod
    public static Object getLock() {
        return ContainerHolder.LOCK;
    }

    private static class ContainerHolder {
        private static final Container CONTAINER = new MapContainer();
        private static final Object LOCK = new Object();
    }
}
