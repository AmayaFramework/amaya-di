package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ScopedProviderBuilderTest extends ServiceProviderBuilderTest {

    public void testScopeOverridesConst(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val1")
                .addScopedInstance("val2")
                .addTransient(S1.class)
                .build();
        var sc = sp.createScoped();
        assertEquals("val1", sp.get(String.class));
        assertEquals("val2", sc.get(String.class));
        assertEquals("val1", sp.get(S1.class).v);
        assertEquals("val2", sc.get(S1.class).v);
    }

    public void testScopeOverridesService(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addTransient(IService.class, BaseService.class)
                .addScopedTransient(IService.class, ScopedService.class)
                .build();
        var sc = sp.createScoped();
        assertEquals("base", sp.get(IService.class).val());
        assertEquals("scoped", sc.get(IService.class).val());
    }

    public void testComplexScopeOverridesService(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addTransient(Dependent.class)
                .addTransient(IService.class, BaseService.class)
                .addScopedTransient(IService.class, ScopedService.class)
                .build();
        var sc = sp.createScoped();
        assertEquals("base", sp.get(Dependent.class).is.val());
        assertEquals("scoped", sc.get(Dependent.class).is.val());
    }

    public void testScopedSingleton(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addTransient(Dependent.class)
                .addTransient(IService.class, BaseService.class)
                .addScopedSingleton(IService.class, BaseService.class)
                .build();
        var sc = sp.createScoped();
        assertEquals("base", sp.get(Dependent.class).is.val());
        assertEquals("base", sc.get(Dependent.class).is.val());
        assertNotSame(sp.get(Dependent.class).is, sp.get(Dependent.class).is);
        assertSame(sc.get(Dependent.class).is, sc.get(Dependent.class).is);
    }

    public void testPromisedType(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addScoped(String.class)
                .addScopedTransient(S1.class)
                .build();
        var sc1 = sp.createScoped();
        sc1.repository().put("v1");
        var sc2 = sp.createScoped();
        sc2.repository().put("v2");
        assertEquals("v1", sc1.get(S1.class).v);
        assertEquals("v2", sc2.get(S1.class).v);
        assertThrows(NullPointerException.class, () -> sp.createScoped().get(S1.class));
    }

    public void testScopedTypeNotFound(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        b.addInstance("v")
                .addTransient(S1.class)
                .addScopedTransient(IntS1.class);
        var t = (Type) null;
        var isS = false;
        try {
            b.build();
        } catch (TypeNotFoundException e) {
            t = e.getType();
            isS = e.isScoped();
        }
        assertEquals(Integer.class, t);
        assertTrue(isS);
    }

    public void testScopedCycle(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        b.addScopedInstance("v")
                .addScopedTransient(S1.class)
                .addScopedTransient(S2.class)
                .addScopedTransient(S3.class)
                .addScopedTransient(IS4.class, S42.class)
                .addScopedTransient(App.class);
        var c = (List<Type>) null;
        var isS = false;
        try {
            b.build();
        } catch (CycleFoundException e) {
            c = e.getCycle();
            isS = e.isScoped();
        }
        assertNotNull(c);
        assertEquals(Set.of(IS4.class, App.class), new HashSet<>(c));
        assertTrue(isS);
    }

    public void testCrossCycle(Supplier<ScopedProviderBuilder> s) {
        var b = s.get();
        b.addInstance("v")
                .addTransient(S1.class)
                .addTransient(S2.class)
                .addTransient(S3.class)
                .addTransient(IS4.class, S41.class)
                .addTransient(App.class)
                .addScopedTransient(IS4.class, S42.class);
        var c = (List<Type>) null;
        var isS = false;
        try {
            b.build();
        } catch (CycleFoundException e) {
            c = e.getCycle();
            isS = e.isScoped();
        }
        assertNotNull(c);
        assertEquals(Set.of(IS4.class, App.class), new HashSet<>(c));
        assertTrue(isS);
    }

    public interface IService {
        String val();
    }

    public static final class Dependent {
        @Inject
        public IService is;
    }

    public static final class IntS1 {
        @Inject
        public Integer i;
    }

    public static final class BaseService implements IService {

        @Override
        public String val() {
            return "base";
        }
    }

    public static final class ScopedService implements IService {

        @Override
        public String val() {
            return "scoped";
        }
    }
}
