package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;

@SuppressWarnings("rawtypes")
final class FieldsObjectFactory implements Function0<Object> {
    private final Function0 constructor;
    private final FieldEntry[] fields;

    FieldsObjectFactory(Function0 constructor, FieldEntry[] fields) {
        this.constructor = constructor;
        this.fields = fields;
    }

    @Override
    public Object invoke() throws Throwable {
        var ret = constructor.invoke();
        for (var entry : fields) {
            entry.field.set(ret, entry.provider.invoke());
        }
        return ret;
    }
}
