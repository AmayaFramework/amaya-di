package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
final class CachedConstructorObjectFactory implements ObjectFactory {
    private final Constructor constructor;
    private final ObjectFactory[] factories;

    CachedConstructorObjectFactory(Constructor constructor, ObjectFactory[] factories) {
        this.constructor = constructor;
        this.factories = factories;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var length = factories.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = factories[i].create(provider);
        }
        return constructor.newInstance(arguments);
    }
}
