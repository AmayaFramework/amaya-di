package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

final class FullObjectFactory implements ObjectFactory {
    private final ObjectFactory constructor;
    private final MethodInvoker[] methods;
    private final FieldEntry[] fields;

    FullObjectFactory(ObjectFactory constructor, MethodInvoker[] methods, FieldEntry[] fields) {
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
            entry.field.set(ret, provider.get(entry.type).create(provider));
        }
        return ret;
    }
}
