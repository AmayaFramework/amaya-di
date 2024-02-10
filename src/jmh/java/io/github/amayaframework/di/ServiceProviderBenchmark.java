package io.github.amayaframework.di;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup
public class ServiceProviderBenchmark {
    private static final ServiceProvider PROVIDER = CheckedProviderBuilder
            .create()
            .addTransient(Service1.class)
            .addTransient(Service2.class)
            .addTransient(Service3.class)
            .addTransient(App.class)
            .build();

    @Benchmark
    public void benchManualInjection(Blackhole blackhole) {
        var app = new App(new Service1());
        app.s2 = new Service2();
        app.setS3(new Service3(new Service1()));
        blackhole.consume(app);
    }

    @Benchmark
    public void benchAmayaInjection(Blackhole blackhole) {
        blackhole.consume(PROVIDER.instantiate(App.class));
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
