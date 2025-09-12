package io.github.amayaframework.di;

import io.github.amayaframework.di.core.Closeable;
import io.github.amayaframework.di.core.LazyCloseableObjectFactory;
import io.github.amayaframework.di.core.LazyObjectFactory;
import io.github.amayaframework.di.reflect.ReflectStubFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public final class ProviderBuilderReflectLazySingletonCloseableTest {

    @AfterEach
    public void reset() {
        SvcCloseable.created.set(0);
        SvcCloseable.closed.set(0);
        SvcPlain.created.set(0);
    }

    @Test
    public void closeableSingletonUsesLazyCloseableAndIsClosedOnProviderClose() {
        var sp = ProviderBuilders.create(new ReflectStubFactory())
                .addSingleton(SvcCloseable.class)
                .build();

        var f = sp.repository().get(SvcCloseable.class);
        assertNotNull(f);
        assertInstanceOf(LazyCloseableObjectFactory.class, f);

        var a = sp.get(SvcCloseable.class);
        var b = sp.get(SvcCloseable.class);
        assertSame(a, b);
        assertEquals(1, SvcCloseable.created.get());

        sp.close();
        assertEquals(1, SvcCloseable.closed.get());

        var c = sp.get(SvcCloseable.class);
        assertNotSame(a, c);
        assertEquals(2, SvcCloseable.created.get());

        sp.close();
        assertEquals(2, SvcCloseable.closed.get());
    }

    @Test
    public void plainSingletonUsesLazyNonCloseableAndNotAffectedByProviderClose() {
        var sp = ProviderBuilders.create(new ReflectStubFactory())
                .addSingleton(SvcPlain.class)
                .build();

        var f = sp.repository().get(SvcPlain.class);
        assertNotNull(f);
        assertInstanceOf(LazyObjectFactory.class, f);
        assertFalse(f instanceof Closeable);

        var a = sp.get(SvcPlain.class);
        var b = sp.get(SvcPlain.class);
        assertSame(a, b);
        assertEquals(1, SvcPlain.created.get());

        sp.close();
        var c = sp.get(SvcPlain.class);
        assertSame(a, c);
        assertEquals(1, SvcPlain.created.get());
    }

    public static final class SvcCloseable implements Closeable {
        static final AtomicInteger created = new AtomicInteger();
        static final AtomicInteger closed = new AtomicInteger();

        public SvcCloseable() {
            created.incrementAndGet();
        }

        @Override
        public void close() {
            closed.incrementAndGet();
        }
    }

    public static final class SvcPlain {
        static final AtomicInteger created = new AtomicInteger();

        public SvcPlain() {
            created.incrementAndGet();
        }
    }
}
