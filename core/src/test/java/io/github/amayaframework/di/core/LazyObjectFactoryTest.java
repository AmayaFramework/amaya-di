package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LazyObjectFactoryTest {

    @Test
    public void testObjectEquality() {
        var factory = (ObjectFactory) p -> new Simple();
        var lazy = new LazyObjectFactory(factory);
        var s1 = lazy.create(null);
        var s2 = lazy.create(null);
        var s3 = lazy.create(null);
        var s4 = lazy.create(null);
        assertEquals(s1, s2);
        assertEquals(s1, s3);
        assertEquals(s1, s4);
        assertEquals(s2, s3);
        assertEquals(s2, s4);
        assertEquals(s3, s4);
    }

    @Test
    public void testCtorCounter() {
        var factory = (ObjectFactory) p -> new WithCounter();
        var lazy = new LazyObjectFactory(factory);
        var s1 = lazy.create(null);
        var s2 = lazy.create(null);
        var s3 = lazy.create(null);
        var s4 = lazy.create(null);
        assertEquals(1, WithCounter.counter);
        assertEquals(s1, s2);
        assertEquals(s1, s3);
        assertEquals(s1, s4);
        assertEquals(s2, s3);
        assertEquals(s2, s4);
        assertEquals(s3, s4);
    }

    public static final class Simple {
    }

    public static final class WithCounter {
        static int counter = 0;

        public WithCounter() {
            ++counter;
        }
    }
}
