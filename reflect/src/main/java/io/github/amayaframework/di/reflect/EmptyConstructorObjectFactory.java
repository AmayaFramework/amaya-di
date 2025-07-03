package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
final class EmptyConstructorObjectFactory implements ObjectFactory {
    private final Constructor constructor;

    EmptyConstructorObjectFactory(Constructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        return constructor.newInstance((Object[]) null);
    }
}
