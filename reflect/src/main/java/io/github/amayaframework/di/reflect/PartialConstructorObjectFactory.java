package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

@SuppressWarnings("rawtypes")
final class PartialConstructorObjectFactory implements ObjectFactory {
    private final Constructor constructor;
    private final Type[] types;
    private final ObjectFactory[] factories;

    PartialConstructorObjectFactory(Constructor constructor, Type[] types, ObjectFactory[] factories) {
        this.constructor = constructor;
        this.types = types;
        this.factories = factories;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var length = factories.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            var factory = factories[i];
            if (factory == null) {
                factory = provider.get(types[i]);
            }
            arguments[i] = factory.create(provider);
        }
        return constructor.newInstance(arguments);
    }
}
