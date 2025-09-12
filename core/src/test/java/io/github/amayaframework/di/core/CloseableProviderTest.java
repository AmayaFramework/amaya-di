package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CloseableProviderTest {

    @Test
    public void testAbstractCloseableProviderClosesFactories() {
        var repo = new HashTypeRepository();
        var factory = new TestCloseableFactory();
        repo.put(TestCloseableFactory.class, factory);

        var provider = new AbstractCloseableProvider(repo) {
            @Override
            public ScopedServiceProvider createScoped() {
                throw new UnsupportedOperationException();
            }
        };

        provider.close();
        assertTrue(factory.closed, "Factory should be closed by provider");
    }

    @Test
    public void testAbstractCloseableScopedProviderClosesOnlyLocal() {
        var parent = new HashTypeRepository();
        var parentFactory = new TestCloseableFactory();
        parent.put(TestCloseableFactory.class, parentFactory);

        var current = new HashTypeRepository();
        var localFactory = new TestCloseableFactory();
        current.put(String.class, localFactory);

        var scopedRepo = new ScopedTypeRepository(current, parent);

        var provider = new AbstractCloseableScopedProvider(scopedRepo) {
            @Override
            public ScopedServiceProvider createScoped() {
                throw new UnsupportedOperationException();
            }
        };

        provider.close();
        assertTrue(localFactory.closed, "Local factory should be closed");
        assertFalse(parentFactory.closed, "Parent factory must not be closed");
    }

    static final class TestCloseableFactory implements CloseableObjectFactory {
        boolean closed = false;

        @Override
        public Object create(TypeProvider provider) {
            return this;
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
