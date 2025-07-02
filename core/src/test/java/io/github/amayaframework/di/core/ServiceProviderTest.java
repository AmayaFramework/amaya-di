package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class ServiceProviderTest {

    @Test
    public void testTransient() {
        var sp = new SPImpl();
        var r = sp.getRepository();
        r.set(String.class, v -> "str");
        r.set(Integer.class, v -> 5);
        r.set(A.class, ofA());
        r.set(B.class, ofB());
        r.set(C.class, ofC());
        var a = sp.get(A.class);
        var b = sp.get(B.class);
        var c = sp.get(C.class);
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        // A unique
        assertNotEquals(a, b.a);
        assertNotEquals(a, c.a);
        assertNotEquals(a, c.b.a);
        // A vals
        assertEquals("str", a.s);
        // B unique
        assertNotEquals(b, c.b);
        // B vals
        assertEquals(5, b.i);
        assertEquals("str", b.a.s);
        // C vals
        assertEquals("str", c.a.s);
        assertEquals("str", c.b.a.s);
        assertEquals(5, c.b.i);
    }

    @Test
    public void testSingleton() {
        var sp = new SPImpl();
        var r = sp.getRepository();
        r.set(String.class, v -> "str");
        r.set(Integer.class, v -> 5);
        r.set(A.class, new LazyObjectFactory(ofA()));
        r.set(B.class, ofB());
        r.set(C.class, ofC());
        var a = sp.get(A.class);
        var b = sp.get(B.class);
        var c = sp.get(C.class);
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        // Check singleton
        assertEquals(a, b.a);
        assertEquals(a, c.a);
        assertEquals(a, c.b.a);
        // A vals
        assertEquals("str", a.s);
        // B unique
        assertNotEquals(b, c.b);
        // B vals
        assertEquals(5, b.i);
        assertEquals("str", b.a.s);
        // C vals
        assertEquals("str", c.a.s);
        assertEquals("str", c.b.a.s);
        assertEquals(5, c.b.i);
    }

    @Test
    public void testScoped() {
        var sp = new SPImpl();
        var r = sp.getRepository();
        r.set(A.class, ofA());
        r.set(String.class, v -> "str");
        var sc = sp.createScoped();
        var sr = sc.getRepository();
        sr.set(String.class, v -> "scopedStr");
        var a = sp.get(A.class);
        var sa = sc.get(A.class);
        assertNotNull(a);
        assertNotNull(sa);
        // A val
        assertEquals("str", a.s);
        // Scoped A val
        assertEquals("scopedStr", sa.s);
    }

    static final class SPImpl extends AbstractServiceProvider {

        private SPImpl(TypeRepository repository) {
            super(repository);
        }

        public SPImpl() {
            super(new HashTypeRepository());
        }

        @Override
        public ServiceProvider createScoped() {
            var cur = new HashTypeRepository();
            var scoped = new ScopedTypeRepository(cur, repository);
            return new SPImpl(scoped);
        }
    }

    static ObjectFactory ofA() {
        return provider -> {
            var s = (String) provider.get(String.class).create(provider);
            return new A(s);
        };
    }

    static ObjectFactory ofB() {
        return provider -> {
            var a = (A) provider.get(A.class).create(provider);
            var i = (Integer) provider.get(Integer.class).create(provider);
            return new B(a, i);
        };
    }

    static ObjectFactory ofC() {
        return provider -> {
            var a = (A) provider.get(A.class).create(provider);
            var b = (B) provider.get(B.class).create(provider);
            return new C(a, b);
        };
    }

    public static final class A {
        String s;

        public A(String s) {
            this.s = s;
        }
    }

    public static final class B {
        A a;
        Integer i;

        public B(A a, Integer i) {
            this.a = a;
            this.i = i;
        }
    }

    public static final class C {
        A a;
        B b;

        public C(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
}
