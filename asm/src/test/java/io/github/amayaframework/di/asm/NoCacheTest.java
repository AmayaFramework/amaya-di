package io.github.amayaframework.di.asm;

import io.github.amayaframework.di.stub.CacheMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class NoCacheTest {

    @Test
    public void testSimpleService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3);
        repo.set(Service1.class, factory.create(Util.S1_SCHEMA, CacheMode.NONE));
        var s1 = Util.get(repo, Service1.class);
        assertNotNull(s1);
        assertEquals(3, s1.i);
        assertEquals("str", s1.str);
    }

    @Test
    public void testNestedService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3);
        repo.set(Service1.class, factory.create(Util.S1_SCHEMA, CacheMode.NONE));
        repo.set(Service3.class, factory.create(Util.S3_SCHEMA, CacheMode.NONE));
        var s3 = Util.get(repo, Service3.class);
        assertNotNull(s3);
        assertNotNull(s3.s1);
        assertEquals(3, s3.s1.i);
        assertEquals("str", s3.s1.str);
    }

    @Test
    public void testComplexService() {
        var factory = new AsmStubFactory();
        var repo = Util.create("str", 3, "str2", 5);
        repo.set(Service1.class, factory.create(Util.S1_SCHEMA, CacheMode.NONE));
        repo.set(Service3.class, factory.create(Util.S3_SCHEMA, CacheMode.NONE));
        repo.set(App.class, factory.create(Util.APP_SCHEMA, CacheMode.NONE));
        var app = Util.get(repo, App.class);
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
