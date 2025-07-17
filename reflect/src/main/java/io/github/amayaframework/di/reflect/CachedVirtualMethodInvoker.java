package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Method;

final class CachedVirtualMethodInvoker implements MethodInvoker {
    private final Method method;
    private final ObjectFactory[] factories;

    CachedVirtualMethodInvoker(Method method, ObjectFactory[] factories) {
        this.method = method;
        this.factories = factories;
    }

    @Override
    public void invoke(Object object, TypeProvider provider) throws Throwable {
        var length = factories.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = factories[i].create(provider);
        }
        method.invoke(object, arguments);
    }
}
