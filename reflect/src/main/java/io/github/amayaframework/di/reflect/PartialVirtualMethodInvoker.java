package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class PartialVirtualMethodInvoker implements MethodInvoker {
    private final Method method;
    private final Type[] types;
    private final ObjectFactory[] factories;

    PartialVirtualMethodInvoker(Method method, Type[] types, ObjectFactory[] factories) {
        this.method = method;
        this.types = types;
        this.factories = factories;
    }

    @Override
    public void invoke(Object object, TypeProvider provider) throws Throwable {
        var length = factories.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            var factory = factories[i];
            if (factory == null) {
                factory = provider.get(types[i]);
            }
            arguments[i] = factory.create(provider);
        }
        method.invoke(object, arguments);
    }
}
