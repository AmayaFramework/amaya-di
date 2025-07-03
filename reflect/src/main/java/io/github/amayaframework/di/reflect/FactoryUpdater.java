package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;

import java.util.function.Consumer;

final class FactoryUpdater implements Consumer<ObjectFactory> {
    final ObjectFactory[] array;
    final int index;

    FactoryUpdater(ObjectFactory[] array, int index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public void accept(ObjectFactory factory) {
        array[index] = factory;
    }
}
