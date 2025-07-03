package io.github.amayaframework.di.asm;

import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class FullCacheTest {

    @Test
    public void testEmptyService() {
        var factory = new AsmStubFactory();
        var eF = factory.create(Util.EMPTY_SCHEMA, CacheMode.FULL);
        var es = Util.get(eF, EmptyService.class);
        assertNotNull(es);
    }

    @Test
    public void testSimpleService() {
        var factory = new AsmStubFactory();
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.FULL);
        s1F.set(String.class, v -> "str");
        s1F.set(Integer.class, v -> 3);
        var s1 = Util.get(s1F, Service1.class);
        assertNotNull(s1);
        assertEquals(3, s1.i);
        assertEquals("str", s1.str);
    }

    @Test
    public void testNestedService() {
        var factory = new AsmStubFactory();
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.FULL);
        var s3F = (CachedObjectFactory) factory.create(Util.S3_SCHEMA, CacheMode.FULL);
        s1F.set(String.class, v -> "str");
        s1F.set(Integer.class, v -> 3);
        s3F.set(Service1.class, s1F);
        var s3 = Util.get(s3F, Service3.class);
        assertNotNull(s3);
        assertNotNull(s3.s1);
        assertEquals(3, s3.s1.i);
        assertEquals("str", s3.s1.str);
    }

    @Test
    public void testComplexService() {
        var factory = new AsmStubFactory();
        var s1F = (CachedObjectFactory) factory.create(Util.S1_SCHEMA, CacheMode.FULL);
        s1F.set(String.class, v -> "str");
        s1F.set(Integer.class, v -> 3);
        var s3F = (CachedObjectFactory) factory.create(Util.S3_SCHEMA, CacheMode.FULL);
        s3F.set(Service1.class, s1F);
        var appF = (CachedObjectFactory) factory.create(Util.APP_SCHEMA, CacheMode.FULL);
        appF.set(Util.S2_STR, v -> new Service2<>("str2"));
        appF.set(Util.S2_INT, v -> new Service2<>(5));
        appF.set(Service1.class, s1F);
        appF.set(Service3.class, s3F);
        var app = Util.get(appF, App.class);
        assertNotNull(app);
        assertNotNull(app.s1);
        assertNotNull(app.s2Int);
        assertNotNull(app.s2String);
        assertNotNull(app.s3);
        assertEquals(3, app.s1.i);
        assertEquals("str", app.s1.str);
        assertEquals("str2", app.s2String.value);
        assertEquals(5, app.s2Int.value);
        assertEquals(3, app.s3.s1.i);
        assertEquals("str", app.s3.s1.str);
    }
}
