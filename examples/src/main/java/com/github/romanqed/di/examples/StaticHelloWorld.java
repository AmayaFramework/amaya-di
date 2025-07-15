package com.github.romanqed.di.examples;

import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

public final class StaticHelloWorld {

    public static void main(String[] args) {
        var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
                .addScoped(String.class) // promised-тип, который должен быть в scope
                .addScopedSingleton(ScopeGreeter.class)
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("scope1");
        var scope2 = provider.createScoped();
        scope2.repository().put("scope2");
        System.out.println(scope1.get(ScopeGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(ScopeGreeter.class).sayHello("Roman"));
    }

    public static final class ScopeGreeter {
        private String scope;

        @Inject
        public static void setScope(ScopeGreeter greeter, String scope) {
            greeter.scope = scope;
        }

        public String sayHello(String name) {
            return "Hello from scope '" + scope + "', " + name + "!";
        }
    }
}
