package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CloseableTest {

    @Test
    public void testAdapterCallsClose() {
        var auto = new TestAutoCloseable();
        var c = Closeable.of(auto);
        c.close();
        assertTrue(auto.closed, "AutoCloseable should be closed via adapter");
    }

    @Test
    public void testAdapterSwallowsExceptions() {
        var auto = new TestAutoCloseable();
        var c = Closeable.of(auto);
        // не должно кидать
        assertDoesNotThrow(c::close);
    }

    @Test
    public void testCustomCloseable() {
        var t = new TestCloseable();
        t.close();
        assertTrue(t.closed, "Closeable should be marked closed");
    }

    static final class TestAutoCloseable implements AutoCloseable {
        boolean closed = false;

        @Override
        public void close() throws Exception {
            closed = true;
            throw new Exception("expected"); // для проверки проглатывания
        }
    }

    static final class TestCloseable implements Closeable {
        boolean closed = false;

        @Override
        public void close() {
            closed = true;
        }
    }
}
