package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.core.ObjectFactory;

public final class ManualHelloWorld {

    public static void main(String[] args) {
        var provider = ProviderBuilders.createScoped()
                .add(IGreeter.class, (ObjectFactory) tp -> new GlobalGreeter())
                .addScoped(IGreeter.class, (ObjectFactory) tp -> {
                    var scope = (String) tp.get(String.class).create(tp);
                    return new ScopedGreeter(scope);
                })
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("Scope One");
        var scope2 = provider.createScoped();
        scope2.repository().put("Scope Two");
        System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
    }

    public interface IGreeter {
        String sayHello(String name);
    }

    public static final class GlobalGreeter implements IGreeter {

        @Override
        public String sayHello(String name) {
            return "Hello, " + name + "!";
        }
    }

    public static final class ScopedGreeter implements IGreeter {
        private final String scope;

        public ScopedGreeter(String scope) {
            this.scope = scope;
        }

        @Override
        public String sayHello(String name) {
            return "Hello from scope '" + scope + "', " + name + "!";
        }
    }
}
