package com.github.romanqed.di.examples;

import com.github.romanqed.jtype.JType;
import com.github.romanqed.jtype.Types;
import io.github.amayaframework.di.BuilderChecks;
import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

import java.util.List;
import java.util.Map;

public final class GenericTypes {

    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory(), BuilderChecks.VALIDATE_MISSING_TYPES)
                .addInstance(new JType<>(){}, List.of("str1", "str2", "str3")) // auto-catch
                .addInstance(Types.of(List.class, Integer.class), List.of(1, 2, 3)) // manual
                // for "new JType<>(){}" type will be Map<String, String>
                .addInstance(new JType<Map<String, Object>>(){}, Map.of("s1", "k1", "s2", "k2")) // and for map
                .addInstance(Types.of(Map.class, Integer.class, Object.class), Map.of(1, 1, 2, 2))
                .addTransient(A1.class)
                .addTransient(A2.class)
                // B<A1> and B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // And, finally, C
                .addTransient(C.class)
                .build();
        var c = provider.get(C.class);
        System.out.println(c.b1.value.ints);
        System.out.println(c.b1.value.strings);
        System.out.println(c.b2.value.intMap);
        System.out.println(c.b2.value.strMap);
    }

    public static final class A1 {
        @Inject
        public List<String> strings;
        @Inject
        public List<Integer> ints;
    }

    public static final class A2 {
        @Inject
        public Map<String, Object> strMap;
        @Inject
        public Map<Integer, Object> intMap;
    }

    public static final class B<T> {
        T value;

        B(T value) {
            this.value = value;
        }
    }

    public static final class C {
        B<A1> b1;
        B<A2> b2;

        public C(B<A1> b1, B<A2> b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }
}
