package io.github.amayaframework.di.asm;

import io.github.amayaframework.di.core.HashTypeRepository;
import io.github.amayaframework.di.core.TypeProvider;
import io.github.amayaframework.di.schema.ReflectSchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup
public class AsmStubBenchmark {
    private static final TypeProvider NO_CACHE_PROVIDER = prepareNoCacheProvider();
    private static final TypeProvider PARTIAL_PROVIDER = preparePartialProvider();
    private static final TypeProvider FULL_PROVIDER = prepareFullProvider();

    private static TypeProvider prepareNoCacheProvider() {
        var factory = new AsmStubFactory();
        var sf = new ReflectSchemaFactory(Inject.class);
        var s1F = factory.create(sf.create(Service1.class), CacheMode.NONE);
        var s2F = factory.create(sf.create(Service2.class), CacheMode.NONE);
        var s3F = factory.create(sf.create(Service3.class), CacheMode.NONE);
        var appF = factory.create(sf.create(App.class), CacheMode.NONE);
        var ret = new HashTypeRepository();
        ret.set(Service1.class, s1F);
        ret.set(Service2.class, s2F);
        ret.set(Service3.class, s3F);
        ret.set(App.class, appF);
        return ret;
    }

    private static TypeProvider preparePartialProvider() {
        var factory = new AsmStubFactory();
        var sf = new ReflectSchemaFactory(Inject.class);
        var s1F = factory.create(sf.create(Service1.class), CacheMode.PARTIAL);
        var s2F = factory.create(sf.create(Service2.class), CacheMode.PARTIAL);
        var s3F = (CachedObjectFactory) factory.create(sf.create(Service3.class), CacheMode.PARTIAL);
        s3F.set(Service1.class, s1F);
        var appF = (CachedObjectFactory) factory.create(sf.create(App.class), CacheMode.PARTIAL);
        appF.set(Service1.class, s1F);
        appF.set(Service3.class, s3F);
        var ret = new HashTypeRepository();
        ret.set(Service2.class, s2F);
        ret.set(App.class, appF);
        return ret;
    }

    private static TypeProvider prepareFullProvider() {
        var factory = new AsmStubFactory();
        var sf = new ReflectSchemaFactory(Inject.class);
        var s1F = factory.create(sf.create(Service1.class), CacheMode.FULL);
        var s2F = factory.create(sf.create(Service2.class), CacheMode.FULL);
        var s3F = (CachedObjectFactory) factory.create(sf.create(Service3.class), CacheMode.FULL);
        s3F.set(Service1.class, s1F);
        var appF = (CachedObjectFactory) factory.create(sf.create(App.class), CacheMode.FULL);
        appF.set(Service1.class, s1F);
        appF.set(Service2.class, s2F);
        appF.set(Service3.class, s3F);
        var ret = new HashTypeRepository();
        ret.set(App.class, appF);
        return ret;
    }

    @Benchmark
    public void benchManualInjection(Blackhole blackhole) {
        var app = new App(new Service1());
        app.s2 = new Service2();
        app.setS3(new Service3(new Service1()));
        blackhole.consume(app);
    }

    @Benchmark
    public void benchNoCacheInjection(Blackhole blackhole) throws Throwable {
        blackhole.consume(NO_CACHE_PROVIDER.get(App.class).create(NO_CACHE_PROVIDER));
    }

    @Benchmark
    public void benchPartialCacheInjection(Blackhole blackhole) throws Throwable {
        blackhole.consume(PARTIAL_PROVIDER.get(App.class).create(PARTIAL_PROVIDER));
    }

    @Benchmark
    public void benchFullCacheInjection(Blackhole blackhole) throws Throwable {
        blackhole.consume(FULL_PROVIDER.get(App.class).create(FULL_PROVIDER));
    }

    public static final class Service1 {
    }

    public static final class Service2 {
    }

    public static final class Service3 {
        final Service1 s1;

        public Service3(Service1 s1) {
            this.s1 = s1;
        }
    }

    public static final class App {
        final Service1 s1;
        @Inject
        public Service2 s2;
        Service3 s3;

        public App(Service1 s1) {
            this.s1 = s1;
        }

        @Inject
        public void setS3(Service3 s3) {
            this.s3 = s3;
        }
    }
}
