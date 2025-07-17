package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;

final class CachedFieldsObjectFactory implements ObjectFactory {
    private final ObjectFactory constructor;
    private final FieldEntry[] fields;

    CachedFieldsObjectFactory(ObjectFactory constructor, FieldEntry[] fields) {
        this.constructor = constructor;
        this.fields = fields;
    }

    @Override
    public Object create(TypeProvider provider) throws Throwable {
        var ret = constructor.create(provider);
        for (var entry : fields) {
            entry.field.set(ret, entry.factory.create(provider));
        }
        return ret;
    }
}
