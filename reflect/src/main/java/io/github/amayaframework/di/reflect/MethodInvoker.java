package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.TypeProvider;

interface MethodInvoker {

    void invoke(Object object, TypeProvider provider) throws Throwable;
}
