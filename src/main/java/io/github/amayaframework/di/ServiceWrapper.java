package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;

/**
 * An interface that describes an abstract mechanism
 * that allows you to proxy or modify providers of service implementations.
 *
 * @param <S> the type of wrapped service
 */
public interface ServiceWrapper<S> extends Function1<Function0<S>, Function0<S>> {

    /**
     * Applies changes to the specified service provider.
     *
     * @param func the specified service provider
     * @return modified service provider
     * @throws Throwable if any errors occur
     */
    @Override
    Function0<S> invoke(Function0<S> func) throws Throwable;
}