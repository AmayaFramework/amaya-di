// ScopedProviderBuilderReflectLazyCloseableTest.java
package io.github.amayaframework.di;

import io.github.amayaframework.di.core.Closeable;
import io.github.amayaframework.di.core.LazyCloseableObjectFactory;
import io.github.amayaframework.di.reflect.ReflectStubFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public final class ScopedProviderBuilderReflectLazyCloseableTest {

    @AfterEach
    public void reset() {
        ScopedCloseable.created.set(0);
        ScopedCloseable.closed.set(0);
    }

    @Test
    public void scopedSingletonIsLazyCloseableAndClosedOnScopeClose() {
        var sp = ProviderBuilders.createScoped(new ReflectStubFactory())
                .addScopedSingleton(ScopedCloseable.class)
                .build();

        var scope1 = sp.createScoped();
        var lf = scope1.repository().getLocal(ScopedCloseable.class);
        assertNotNull(lf);
        assertInstanceOf(LazyCloseableObjectFactory.class, lf);

        var a = scope1.get(ScopedCloseable.class);
        var b = scope1.get(ScopedCloseable.class);
        assertSame(a, b);
        assertEquals(1, ScopedCloseable.created.get());

        scope1.close();
        assertEquals(1, ScopedCloseable.closed.get());

        var scope2 = sp.createScoped();
        var c = scope2.get(ScopedCloseable.class);
        assertNotSame(a, c);
        assertEquals(2, ScopedCloseable.created.get());
        scope2.close();
        assertEquals(2, ScopedCloseable.closed.get());

        sp.close();
        assertEquals(2, ScopedCloseable.closed.get());
    }

    public static final class ScopedCloseable implements Closeable {
        static final AtomicInteger created = new AtomicInteger();
        static final AtomicInteger closed = new AtomicInteger();

        public ScopedCloseable() {
            created.incrementAndGet();
        }

        @Override
        public void close() {
            closed.incrementAndGet();
        }
    }
}
