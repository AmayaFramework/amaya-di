package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Runnable1;

import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
final class VirtualMethodInvoker implements Runnable1 {
    private final Method method;
    private final Function0[] providers;

    VirtualMethodInvoker(Method method, Function0[] providers) {
        this.method = method;
        this.providers = providers;
    }

    @Override
    public void run(Object object) throws Throwable {
        var length = providers.length;
        var arguments = new Object[length];
        for (var i = 0; i < length; ++i) {
            arguments[i] = providers[i].invoke();
        }
        method.invoke(object, arguments);
    }
}
