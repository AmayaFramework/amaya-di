package io.github.amayaframework.di;

import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceProviderBuilderTest {

    public void testSimpleInstance(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val")
                .build();
        assertEquals("val", sp.get(String.class));
    }

    public void testComplexType(Supplier<ServiceProviderBuilder> s) {
        var ctI = new JType<List<Integer>>(){};
        var ctS = new JType<List<String>>(){};
        var b = s.get();
        var sp = b
                .addInstance(ctS, List.of("a", "b", "c"))
                .addInstance(ctI, List.of(1, 2, 3))
                .build();
        assertEquals("a", sp.get(ctS).get(0));
        assertEquals("b", sp.get(ctS).get(1));
        assertEquals("c", sp.get(ctS).get(2));
        assertEquals(1, sp.get(ctI).get(0));
        assertEquals(2, sp.get(ctI).get(1));
        assertEquals(3, sp.get(ctI).get(2));
    }

    @Test
    public void testSimpleInstance() {
        testSimpleInstance(ProviderBuilders::create);
        testSimpleInstance(ProviderBuilders::createChecked);
        var noOpSchema = (SchemaFactory) c -> null;
        var noOpStub = (StubFactory) (sc, m) -> null;
        testSimpleInstance(() -> new PlainScopedProviderBuilder(noOpSchema, noOpStub, CacheMode.NONE));
        testSimpleInstance(() -> new CheckedScopedProviderBuilder(noOpSchema, noOpStub, CacheMode.NONE, 0));
    }

    @Test
    public void testComplexType() {
        testComplexType(ProviderBuilders::create);
        testComplexType(ProviderBuilders::createChecked);
        var noOpSchema = (SchemaFactory) c -> null;
        var noOpStub = (StubFactory) (sc, m) -> null;
        testComplexType(() -> new PlainScopedProviderBuilder(noOpSchema, noOpStub, CacheMode.NONE));
        testComplexType(() -> new CheckedScopedProviderBuilder(noOpSchema, noOpStub, CacheMode.NONE, 0));
    }

    public void testSingleService(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp1 = b
                .addInstance("val1")
                .addTransient(S1.class)
                .build();
        var sp2 = b
                .addInstance("val2")
                .addSingleton(S1.class)
                .build();
        assertEquals("val1", sp1.get(S1.class).v);
        assertNotSame(sp1.get(S1.class), sp1.get(S1.class));
        assertEquals("val2", sp2.get(S1.class).v);
        assertSame(sp2.get(S1.class), sp2.get(S1.class));
    }

    public void testTwoServices(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val")
                .addTransient(S1.class)
                .addTransient(S2.class)
                .build();
        assertEquals("val", sp.get(S1.class).v);
        assertEquals("val", sp.get(S2.class).s1.v);
    }

    public void testComplexServices(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val")
                .addTransient(S1.class)
                .addTransient(S2.class)
                .addTransient(S3.class)
                .addTransient(IS4.class, S41.class)
                .build();
        assertEquals("val", sp.get(S1.class).v);
        assertEquals("val", sp.get(S2.class).s1.v);
        assertEquals("val", sp.get(S3.class).s1.v);
        assertEquals("val", sp.get(IS4.class).s3().s1.v);
        assertNull(sp.get(S41.class));
    }

    public void testApp(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val")
                .addTransient(S1.class)
                .addTransient(S2.class)
                .addTransient(S3.class)
                .addTransient(IS4.class, S41.class)
                .addTransient(App.class)
                .build();
        assertEquals("val", sp.get(S1.class).v);
        assertEquals("val", sp.get(S2.class).s1.v);
        assertEquals("val", sp.get(S3.class).s1.v);
        assertEquals("val", sp.get(IS4.class).s3().s1.v);
        assertNull(sp.get(S41.class));
        var app = sp.get(App.class);
        assertEquals("val", app.s1.v);
        assertEquals("val", app.s2.s1.v);
        assertEquals("val", app.s3.s1.v);
        assertEquals("val", app.s4.s3().s1.v);
    }

    public void testScopeOverride(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .withCacheMode(CacheMode.NONE)
                .addInstance("val1")
                .addTransient(S1.class)
                .build();
        var sc = sp.createScoped();
        sc.repository().put("val2");
        assertEquals("val1", sp.get(S1.class).v);
        assertEquals("val2", sc.get(S1.class).v);
    }

    public void testMissingType(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b.addTransient(S1.class);
        var t = (Type) null;
        try {
            sp.build();
        } catch (TypeNotFoundException e) {
            t = e.getType();
        }
        assertEquals(String.class, t);
    }

    public void testCycle(Supplier<ServiceProviderBuilder> s) {
        var b = s.get();
        var sp = b
                .addInstance("val")
                .addTransient(S1.class)
                .addTransient(S2.class)
                .addTransient(S3.class)
                .addTransient(IS4.class, S42.class)
                .addTransient(App.class);
        var c = (List<Type>) null;
        try {
            sp.build();
        } catch (CycleFoundException e) {
            c = e.getCycle();
        }
        assertNotNull(c);
        assertEquals(Set.of(IS4.class, App.class), new HashSet<>(c));
    }

    public static final class S1 {
        final String v;

        public S1(String v) {
            this.v = v;
        }
    }

    public static final class S2 {
        @Inject
        public S1 s1;
    }

    public static final class S3 {
        final S1 s1;

        public S3(S1 s1) {
            this.s1 = s1;
        }
    }

    public interface IS4 {
        S3 s3();
    }

    public static final class S41 implements IS4 {
        S3 s3;

        @Inject
        public void setS3(S3 s3) {
            this.s3 = s3;
        }

        @Override
        public S3 s3() {
            return s3;
        }
    }

    public static final class S42 implements IS4 {
        @Inject
        public App app;

        @Inject
        public S3 s3;

        @Override
        public S3 s3() {
            return s3;
        }
    }

    public static final class App {
        @Inject
        public S1 s1;
        final S2 s2;
        S3 s3;
        IS4 s4;

        public App(S2 s2) {
            this.s2 = s2;
        }

        @Inject
        public void setS3(S3 s3) {
            this.s3 = s3;
        }

        @Inject
        public static void setS4(App app, IS4 is4) {
            app.s4 = is4;
        }
    }
}
