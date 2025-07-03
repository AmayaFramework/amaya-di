package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

final class PartialFullObjectFactory implements ObjectFactory {
    private final ObjectFactory constructor;
    private final MethodInvoker[] methods;
    private final FieldEntry[] fields;

    PartialFullObjectFactory(ObjectFactory constructor, MethodInvoker[] methods, FieldEntry[] fields) {
        this.constructor = constructor;
        this.methods = methods;
        this.fields = fields;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var ret = constructor.create(provider);
        for (var method : methods) {
            method.invoke(ret, provider);
        }
        for (var entry : fields) {
            var factory = entry.factory;
            if (factory == null) {
                factory = provider.get(entry.type);
            }
            entry.field.set(ret, factory.create(provider));
        }
        return ret;
    }
}
