package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class LazyCloseableObjectFactoryTest {

    @Test
    public void testValueIsClosed() throws Throwable {
        var obj = new TestCloseable();
        var factory = new LazyCloseableObjectFactory(p -> obj);

        var created = factory.create(null);
        assertSame(obj, created);

        factory.close();
        assertTrue(obj.closed, "Underlying object should be closed");
    }

    @Test
    public void testDelegateFactoryIsClosed() {
        var delegate = new CloseableObjectFactory() {
            public boolean closed = false;

            @Override
            public Object create(TypeProvider provider) {
                return null;
            }

            @Override
            public void close() {
                closed = true;
            }
        };
        var factory = new LazyCloseableObjectFactory(delegate);
        factory.close();
        assertTrue(delegate.closed, "Delegate factory should be closed");
    }

    @Test
    public void testCloseIsIdempotent() throws Throwable {
        var obj = new TestCloseable();
        var factory = new LazyCloseableObjectFactory(p -> obj);
        factory.create(null);

        factory.close();
        assertTrue(obj.closed);

        obj.closed = false;
        factory.close(); // второй вызов не должен заново ломать
        assertFalse(obj.closed, "Second close should not re-close the object");
    }

    static final class TestCloseable implements Closeable {
        boolean closed = false;

        @Override
        public void close() {
            closed = true;
        }
    }
}
