package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class VirtualMethodInvoker implements MethodInvoker {
    private final Method method;
    private final Type[] types;

    VirtualMethodInvoker(Method method, Type[] types) {
        this.method = method;
        this.types = types;
    }

    @Override
    public void invoke(Object object, TypeProvider provider) throws Throwable {
        var length = types.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = provider.get(types[i]).create(provider);
        }
        method.invoke(object, arguments);
    }
}
