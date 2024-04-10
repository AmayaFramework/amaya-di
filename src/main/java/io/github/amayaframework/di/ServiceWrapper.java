package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;

public interface ServiceWrapper<S> extends Function1<Function0<S>, Function0<S>> {

    @Override
    Function0<S> invoke(Function0<S> func) throws Throwable;
}