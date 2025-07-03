package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;
import io.github.amayaframework.di.stub.CachedObjectFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;

final class UpdatedObjectFactory implements CachedObjectFactory {
    private final ObjectFactory factory;
    private final Map<Type, Consumer<ObjectFactory>> updaters;

    UpdatedObjectFactory(ObjectFactory factory, Map<Type, Consumer<ObjectFactory>> updaters) {
        this.factory = factory;
        this.updaters = updaters;
    }

    @Override
    public void set(Type type, ObjectFactory factory) {
        var updater = updaters.get(type);
        if (updater == null) {
            return;
        }
        updater.accept(factory);
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        return factory.create(provider);
    }
}
