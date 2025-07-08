package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

public final class SimpleHelloWorld {

    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory())
                .addInstance("Hello, world!")
                .build();
        System.out.println(provider.get(String.class));
    }
}
