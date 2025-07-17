package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

@SuppressWarnings("rawtypes")
final class ConstructorObjectFactory implements ObjectFactory {
    private final Constructor constructor;
    private final Type[] types;

    ConstructorObjectFactory(Constructor constructor, Type[] types) {
        this.constructor = constructor;
        this.types = types;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var length = types.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = provider.get(types[i]).create(provider);
        }
        return constructor.newInstance(arguments);
    }
}
