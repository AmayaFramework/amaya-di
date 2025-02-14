package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
final class ConstructorObjectFactory implements Function0 {
    private final Constructor constructor;
    private final Function0[] providers;

    ConstructorObjectFactory(Constructor constructor, Function0[] providers) {
        this.constructor = constructor;
        this.providers = providers;
    }

    @Override
    public Object invoke() throws Throwable {
        var length = providers.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = providers[i].invoke();
        }
        return constructor.newInstance(arguments);
    }
}
