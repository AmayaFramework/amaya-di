package io.github.amayaframework.di.asm;

import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class PartialCacheTest {

    @Test
    public void testSimpleService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3);
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.PARTIAL);
        s1F.set(String.class, v -> "str1");
        repo.set(Service1.class, s1F);
        var s1 = Util.get(repo, Service1.class);
        assertNotNull(s1);
        assertEquals(3, s1.i);
        assertEquals("str1", s1.str);
    }

    @Test
    public void testNestedService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3);
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.PARTIAL);
        s1F.set(String.class, v -> "str1");
        repo.set(Service1.class, s1F);
        repo.set(Service3.class, factory.create(Util.S3_SCHEMA, CacheMode.PARTIAL));
        var s3 = Util.get(repo, Service3.class);
        assertNotNull(s3);
        assertNotNull(s3.s1);
        assertEquals(3, s3.s1.i);
        assertEquals("str1", s3.s1.str);
    }

    @Test
    public void testComplexService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3, "str2", 9);
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.PARTIAL);
        s1F.set(String.class, v -> "str1");
        var appF = (CachedObjectFactory) factory.create(Util.APP_SCHEMA, CacheMode.PARTIAL);
        appF.set(Util.S2_INT, v -> new Service2<>(12));
        var s3F = (CachedObjectFactory) factory.create(Util.S3_SCHEMA, CacheMode.PARTIAL);
        s3F.set(Service1.class, v -> new Service1("str", 4));
        repo.set(Service1.class, s1F);
        repo.set(Service3.class, s3F);
        repo.set(App.class, appF);
        var app = Util.get(repo, App.class);
        assertNotNull(app);
        assertNotNull(app.s1);
        assertNotNull(app.s2Int);
        assertNotNull(app.s2String);
        assertNotNull(app.s3);
        assertEquals(3, app.s1.i);
        assertEquals("str1", app.s1.str);
        assertEquals("str2", app.s2String.value);
        assertEquals(12, app.s2Int.value);
        assertEquals(4, app.s3.s1.i);
        assertEquals("str", app.s3.s1.str);
    }
}
