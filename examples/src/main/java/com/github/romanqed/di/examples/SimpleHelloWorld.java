package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;

public final class SimpleHelloWorld {

    public static void main(String[] args) {
        var provider = ProviderBuilders.create()
                .addInstance("Hello, world!")
                .build();
        System.out.println(provider.get(String.class));
    }
}
