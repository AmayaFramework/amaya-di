package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.reflect.ReflectStubFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup
public class ReflectStubBenchmark {
    private static final StubFactory FACTORY = new ReflectStubFactory();
    private static final ServiceProvider NO_CACHE_PROVIDER = prepareProvider(CacheMode.NONE);
    private static final ServiceProvider PARTIAL_PROVIDER = prepareProvider(CacheMode.PARTIAL);
    private static final ServiceProvider FULL_PROVIDER = prepareProvider(CacheMode.FULL);
    private static final ServiceProvider SCOPED_PROVIDER = prepareScopedProvider();

    private static ServiceProvider prepareProvider(CacheMode mode) {
        var builder = ProviderBuilders.create(FACTORY);
        return builder
                .withCacheMode(mode)
                .addTransient(Service1.class)
                .addTransient(Service2.class)
                .addTransient(Service3.class)
                .addTransient(App.class)
                .build();
    }

    private static ServiceProvider prepareScopedProvider() {
        var builder = ProviderBuilders.createScoped(FACTORY);
        return builder
                .addTransient(Service1.class)
                .addScopedSingleton(Service1.class)
                .addTransient(Service2.class)
                .addTransient(Service3.class)
                .addTransient(App.class)
                .build();
    }

    @Benchmark
    public void benchManualInjection(Blackhole blackhole) {
        var app = new App(new Service1());
        app.s2 = new Service2();
        app.setS3(new Service3(new Service1()));
        blackhole.consume(app);
    }

    @Benchmark
    public void benchNoCacheInjection(Blackhole blackhole) {
        blackhole.consume(NO_CACHE_PROVIDER.get(App.class));
    }

    @Benchmark
    public void benchPartialCacheInjection(Blackhole blackhole) {
        blackhole.consume(PARTIAL_PROVIDER.get(App.class));
    }

    @Benchmark
    public void benchFullCacheInjection(Blackhole blackhole) {
        blackhole.consume(FULL_PROVIDER.get(App.class));
    }

    @Benchmark
    public void benchScopeCreationAndInjection(Blackhole blackhole) {
        var scope = SCOPED_PROVIDER.createScoped();
        blackhole.consume(scope.get(App.class));
    }
}
