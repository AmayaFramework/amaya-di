package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Runnable1;

@SuppressWarnings("rawtypes")
final class MethodsObjectFactory implements Function0<Object> {
    private final Function0 constructor;
    private final Runnable1[] methods;

    MethodsObjectFactory(Function0 constructor, Runnable1[] methods) {
        this.constructor = constructor;
        this.methods = methods;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke() throws Throwable {
        var ret = constructor.invoke();
        for (var method : methods) {
            method.run(ret);
        }
        return ret;
    }
}
