package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

final class MethodsObjectFactory implements ObjectFactory {
    private final ObjectFactory constructor;
    private final MethodInvoker[] methods;

    MethodsObjectFactory(ObjectFactory constructor, MethodInvoker[] methods) {
        this.constructor = constructor;
        this.methods = methods;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var ret = constructor.create(provider);
        for (var method : methods) {
            method.invoke(ret, provider);
        }
        return ret;
    }
}
