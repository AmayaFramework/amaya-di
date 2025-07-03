package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Method;

final class CachedStaticMethodInvoker implements MethodInvoker {
    private final Method method;
    private final ObjectFactory[] factories;

    CachedStaticMethodInvoker(Method method, ObjectFactory[] factories) {
        this.method = method;
        this.factories = factories;
    }

    @Override
    public void invoke(Object object, TypeProvider provider) throws Throwable {
        var length = factories.length;
        var arguments = new Object[length + 1];
        arguments[0] = object;
        for (var i = 0; i < length; ++i) {
            arguments[i + 1] = factories[i].create(provider);
        }
        method.invoke(null, arguments);
    }
}
