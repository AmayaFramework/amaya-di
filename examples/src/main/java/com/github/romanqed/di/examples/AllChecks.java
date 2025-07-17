package com.github.romanqed.di.examples;

import io.github.amayaframework.di.CycleFoundException;
import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.TypeNotFoundException;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

public final class AllChecks {

    public static void main(String[] args) {
        // No base type
        var builder = ProviderBuilders.createChecked(new ReflectStubFactory());
        builder.addTransient(StringA.class);
        try {
            builder.build();
        } catch (TypeNotFoundException e) {
            System.out.println(e.getMessage());
        }
        // No scoped type
        var scopedBuilder = ProviderBuilders.createCheckedScoped(new ReflectStubFactory());
        scopedBuilder.addScopedTransient(StringA.class);
        try {
            scopedBuilder.build();
        } catch (TypeNotFoundException e) {
            System.out.println(e.getMessage());
        }
        // Base cycle
        builder.addTransient(A.class);
        builder.addTransient(B.class);
        builder.addTransient(IC.class, CycledC.class);
        try {
            builder.build();
        } catch (CycleFoundException e) {
            System.out.println(e.getMessage());
        }
        // Scoped cycle
        scopedBuilder.addScopedTransient(A.class);
        scopedBuilder.addScopedTransient(B.class);
        scopedBuilder.addScopedTransient(IC.class, CycledC.class);
        try {
            scopedBuilder.build();
        } catch (CycleFoundException e) {
            System.out.println(e.getMessage());
        }
        // Cross cycle
        scopedBuilder.addTransient(A.class);
        scopedBuilder.addTransient(B.class);
        scopedBuilder.addTransient(IC.class, NoCycleC.class);
        scopedBuilder.addScopedTransient(IC.class, CycledC.class);
        try {
            scopedBuilder.build();
        } catch (CycleFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static final class StringA {
        @Inject
        public String v;
    }

    public interface IC {}

    public static final class A {
        @Inject
        public B b;
    }

    public static final class B {
        @Inject
        public IC c;
    }

    public static final class NoCycleC implements IC {}

    public static final class CycledC implements IC {
        @Inject
        public A a;
    }
}
