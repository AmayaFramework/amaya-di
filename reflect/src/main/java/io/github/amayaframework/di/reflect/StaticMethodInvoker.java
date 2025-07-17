package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.TypeProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class StaticMethodInvoker implements MethodInvoker {
    private final Method method;
    private final Type[] types;

    StaticMethodInvoker(Method method, Type[] types) {
        this.method = method;
        this.types = types;
    }

    @Override
    public void invoke(Object object, TypeProvider provider) throws Throwable {
        var length = types.length;
        var arguments = new Object[length + 1];
        arguments[0] = object;
        for (var i = 0; i < length; ++i) {
            arguments[i + 1] = provider.get(types[i]).create(provider);
        }
        method.invoke(null, arguments);
    }
}
