package io.github.amayaframework.di;

import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.MapContainer;
import io.github.amayaframework.di.containers.Provider;

public class ContainerProvider {
    @Provider
    public static Container get() {
        return ContainerHolder.CONTAINER;
    }

    private static class ContainerHolder {
        private static final Container CONTAINER = new MapContainer();
    }
}
