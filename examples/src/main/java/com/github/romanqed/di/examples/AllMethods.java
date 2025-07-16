package com.github.romanqed.di.examples;

import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.ServiceWrapper;
import io.github.amayaframework.di.core.LazyObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class AllMethods {

    public static void main(String[] args) {
        var builder = ProviderBuilders.createScoped(new ReflectStubFactory());

        // Base container

        // User's object factory impl
        builder.add(Object.class, (ObjectFactory)  tp -> "base");
        // Function0 variant (Callable alternative)
        builder.add(Integer.class, () -> 5);
        builder.add(Map.class, (Function0<?>) HashMap::new);
        // Instances
        builder.addInstance(Float.class, 5f);
        builder.addInstance(10d); // as Double
        // Generic add methods for autogen
        builder.add(IA.class, A1.class, generatedObjectFactory -> {
            System.out.println("Apply wrapper to IA (A1)");
            return typeProvider -> {
                System.out.println("Create IA (A1) instance here");
                return generatedObjectFactory.create(typeProvider);
            };
        });
        builder.add(A1.class, (ServiceWrapper)  generatedObjectFactory -> {
            System.out.println("Apply wrapper to A1");
            return typeProvider -> {
                System.out.println("Create A1 instance here");
                return generatedObjectFactory.create(typeProvider);
            };
        });
        builder.add(ManualTransient.class, (ServiceWrapper) null); // your own transient
        builder.add(ManualSingleton.class, LazyObjectFactory::new); // your own singleton
        // Transient & Singleton methods
        builder.addTransient(Transient.class);
        builder.addSingleton(Singleton.class);

        // Scoped container

        // Promised types, user must provide it after scope creation
        // Used mostly for graph validation
        builder.addScoped(Scoped.class);
        // Scoped object factory impl: accessible only in scope
        builder.addScoped(Object.class, (ObjectFactory)  tp -> "scoped"); // overrides base types
        // Function0 variant (Callable alternative)
        builder.addScoped(Integer.class, () -> 10);
        builder.addScoped(Map.class, (Function0<?>) TreeMap::new);
        // Instances
        builder.addScopedInstance(Float.class, 10f);
        builder.addScopedInstance(5d); // as Double
        // Wrapped object factory impl: wrapper will be applied after scope creation
        builder.addScoped(ScopedManual.class, tp -> new ScopedManual(), yourObjectFactory -> {
            System.out.println("Apply scoped wrapper to ScopedManual");
            return typeProvider -> {
                System.out.println("Create scoped ScopedManual instance");
                return yourObjectFactory.create(typeProvider);
            };
        });
        // Generic addScoped methods for autogen
        builder.addScoped(IA.class, A2.class, generatedObjectFactory -> {
            System.out.println("Apply scoped wrapper to IA (A2)");
            return typeProvider -> {
                System.out.println("Create IA (A2) instance here");
                return generatedObjectFactory.create(typeProvider);
            };
        });
        builder.addScoped(A2.class, (ServiceWrapper)  generatedObjectFactory -> {
            System.out.println("Apply scoped wrapper to A2");
            return typeProvider -> {
                System.out.println("Create A2 instance here");
                return generatedObjectFactory.create(typeProvider);
            };
        });
        builder.addScoped(ScopedManualTransient.class, (ServiceWrapper) null); // your own scoped transient
        builder.addScoped(ScopedManualSingleton.class, LazyObjectFactory::new); // your own scoped singleton
        // Scoped Transient & Singleton methods
        builder.addScopedTransient(ScopedTransient.class);
        builder.addScopedSingleton(ScopedSingleton.class);

        var provider = builder.build();

        // Base
        System.out.println("Created base container\n\n");
        System.out.println("Object: " + provider.get(Object.class));
        System.out.println("Integer: " + provider.get(Integer.class));
        System.out.println("Map: " + provider.get(Map.class).getClass());
        System.out.println("Float: " + provider.get(Float.class));
        System.out.println("Double: " + provider.get(Double.class));
        System.out.println("IA: " + provider.get(IA.class));
        System.out.println("A1: " + provider.get(A1.class));
        System.out.println(
                "Manual transient: " + provider.get(ManualTransient.class) + ", " + provider.get(ManualTransient.class)
        );
        System.out.println(
                "Manual singleton: " + provider.get(ManualSingleton.class) + ", " + provider.get(ManualSingleton.class)
        );
        System.out.println("Transient: " + provider.get(Transient.class) + ", " + provider.get(Transient.class));
        System.out.println("Singleton: " + provider.get(Singleton.class) + ", " + provider.get(Singleton.class));
        // Request scoped types from base container
        System.out.println("Scoped: " + provider.get(Scoped.class));
        System.out.println("Scoped manual: " + provider.get(ScopedManual.class));
        System.out.println("Scoped manual transient: " + provider.get(ScopedManualTransient.class));
        System.out.println("Scoped manual singleton: " + provider.get(ScopedManualSingleton.class));
        System.out.println("Scoped transient: " + provider.get(ScopedTransient.class));
        System.out.println("Scoped singleton: " + provider.get(ScopedSingleton.class));

        // Scoped
        System.out.println("\n\n");
        var scope = provider.createScoped();
        // Add promised type
        scope.repository().put(new Scoped());
        System.out.println("Created scoped container\n\n");
        System.out.println("Object: " + scope.get(Object.class));
        System.out.println("Integer: " + scope.get(Integer.class));
        System.out.println("Map: " + scope.get(Map.class).getClass());
        System.out.println("Float: " + scope.get(Float.class));
        System.out.println("Double: " + scope.get(Double.class));
        System.out.println("IA: " + scope.get(IA.class));
        System.out.println("A2: " + scope.get(A2.class));
        System.out.println("Scoped: " + scope.get(Scoped.class));
        System.out.println("Scoped manual: " + scope.get(ScopedManual.class));
        System.out.println(
                "Scoped manual transient: " + scope.get(ScopedManualTransient.class) + ", " + scope.get(ScopedManualTransient.class)
        );
        System.out.println(
                "Scoped manual singleton: " + scope.get(ScopedManualSingleton.class) + ", " + scope.get(ScopedManualSingleton.class)
        );
        System.out.println(
                "Scoped transient: " + scope.get(ScopedTransient.class) + ", " + scope.get(ScopedTransient.class)
        );
        System.out.println(
                "Scoped singleton: " + scope.get(ScopedSingleton.class) + ", " + scope.get(ScopedSingleton.class)
        );
    }

    public interface IA {}

    public static final class A1 implements IA {

        @Override
        public String toString() {
            return "A1@" + hashCode();
        }
    }

    public static final class A2 implements IA {
        @Override
        public String toString() {
            return "A2@" + hashCode();
        }
    }

    public static final class ManualTransient {
        @Override
        public String toString() {
            return "MT@" + hashCode();
        }
    }

    public static final class ManualSingleton {
        @Override
        public String toString() {
            return "MS@" + hashCode();
        }
    }

    public static final class Transient {
        @Override
        public String toString() {
            return "T@" + hashCode();
        }
    }

    public static final class Singleton {
        @Override
        public String toString() {
            return "S@" + hashCode();
        }
    }

    public static final class Scoped {
        @Override
        public String toString() {
            return "Scoped@" + hashCode();
        }
    }

    public static final class ScopedManual {
        @Override
        public String toString() {
            return "ScopedM@" + hashCode();
        }
    }

    public static final class ScopedManualTransient {
        @Override
        public String toString() {
            return "ScopedMT@" + hashCode();
        }
    }

    public static final class ScopedManualSingleton {
        @Override
        public String toString() {
            return "ScopedMS@" + hashCode();
        }
    }

    public static final class ScopedTransient {
        @Override
        public String toString() {
            return "ScopedT@" + hashCode();
        }
    }

    public static final class ScopedSingleton {
        @Override
        public String toString() {
            return "ScopedS@" + hashCode();
        }
    }
}
