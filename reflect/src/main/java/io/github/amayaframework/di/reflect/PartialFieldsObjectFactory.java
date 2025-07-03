package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

final class PartialFieldsObjectFactory implements ObjectFactory {
    private final ObjectFactory constructor;
    private final FieldEntry[] fields;

    PartialFieldsObjectFactory(ObjectFactory constructor, FieldEntry[] fields) {
        this.constructor = constructor;
        this.fields = fields;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var ret = constructor.create(provider);
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
