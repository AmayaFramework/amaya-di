package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

final class WrappedEntry {
    final ObjectFactory factory;
    final ServiceWrapper wrapper;

    WrappedEntry(ObjectFactory factory, ServiceWrapper wrapper) {
        this.factory = factory;
        this.wrapper = wrapper;
    }
}
