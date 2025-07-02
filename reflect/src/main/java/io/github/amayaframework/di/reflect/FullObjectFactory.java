package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Runnable1;

@SuppressWarnings("rawtypes")
final class FullObjectFactory implements Function0<Object> {
    private final Function0 constructor;
    private final Runnable1[] methods;
    private final FieldEntry[] fields;

    FullObjectFactory(Function0 constructor, Runnable1[] methods, FieldEntry[] fields) {
        this.constructor = constructor;
        this.methods = methods;
        this.fields = fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke() throws Throwable {
        var ret = constructor.invoke();
        for (var method : methods) {
            method.run(ret);
        }
        for (var entry : fields) {
            entry.field.set(ret, entry.provider.invoke());
        }
        return ret;
    }
}
