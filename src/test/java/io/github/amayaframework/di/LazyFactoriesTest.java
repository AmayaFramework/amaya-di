package io.github.amayaframework.di;

import io.github.amayaframework.di.stub.CacheMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LazyFactoriesTest {

    @Test
    public void testNoClasses() {
        var pb = new PlainProviderBuilder(null, null, CacheMode.FULL);
        var cb = new CheckedProviderBuilder(null, null, CacheMode.FULL, 0);
        pb.addInstance("pb");
        cb.addInstance("cb");
        assertEquals("pb", pb.build().get(String.class));
        assertEquals("cb", cb.build().get(String.class));
    }

    @Test
    public void testWithClasses() {
        var pb = new PlainProviderBuilder(null, null, CacheMode.FULL);
        var cb = new CheckedProviderBuilder(null, null, CacheMode.FULL, 0);
        pb.addInstance("pb");
        pb.addTransient(LazyFactoriesTest.class);
        cb.addInstance("cb");
        cb.addTransient(LazyFactoriesTest.class);
        assertThrows(IllegalStateException.class, pb::build);
        assertThrows(IllegalStateException.class, cb::build);
    }

    @Test
    public void testScopedNoClasses() {
        var pb = new PlainScopedProviderBuilder(null, null, CacheMode.FULL);
        var cb = new CheckedScopedProviderBuilder(null, null, CacheMode.FULL, 0);
        pb.addInstance("pb");
        pb.addScopedInstance("pbScoped");
        cb.addInstance("cb");
        cb.addScopedInstance("cbScoped");
        var pp = pb.build();
        var cp = cb.build();
        assertEquals("pb", pp.get(String.class));
        assertEquals("cb", cp.get(String.class));
        assertEquals("pbScoped", pp.createScoped().get(String.class));
        assertEquals("cbScoped", cp.createScoped().get(String.class));
    }

    @Test
    public void testScopedWithClasses() {
        var pb = new PlainScopedProviderBuilder(null, null, CacheMode.FULL);
        var cb = new CheckedScopedProviderBuilder(null, null, CacheMode.FULL, 0);
        pb.addInstance("pb");
        pb.addScopedTransient(LazyFactoriesTest.class);
        cb.addInstance("cb");
        cb.addScopedTransient(LazyFactoriesTest.class);
        assertThrows(IllegalStateException.class, pb::build);
        assertThrows(IllegalStateException.class, cb::build);
    }
}
