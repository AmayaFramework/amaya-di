package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

public final class WrappedEntry {
    public final ObjectFactory factory;
    public final ServiceWrapper wrapper;

    public WrappedEntry(ObjectFactory factory, ServiceWrapper wrapper) {
        this.factory = factory;
        this.wrapper = wrapper;
    }
}
