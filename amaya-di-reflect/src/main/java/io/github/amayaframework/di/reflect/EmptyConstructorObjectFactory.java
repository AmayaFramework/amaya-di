package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
final class EmptyConstructorObjectFactory implements Function0 {
    private final Constructor constructor;

    EmptyConstructorObjectFactory(Constructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object invoke() throws Throwable {
        return constructor.newInstance((Object[]) null);
    }
}
