package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;
import io.github.amayaframework.di.internal.PlainScopedServiceProvider;
import io.github.amayaframework.di.internal.PlainServiceProvider;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup
public class CloseableBenchmark {
    private static final ServiceProvider PROVIDER = prepareProvider();
    private static final ScopedServiceProvider SCOPED_PROVIDER = prepareScopedProvider();

    private static ServiceProvider prepareProvider() {
        var repo = new HashTypeRepository();
        repo.putCloseable(new A());
        repo.putCloseable(new B());
        repo.put(new C());
        return new PlainServiceProvider(repo);
    }

    private static ScopedServiceProvider prepareScopedProvider() {
        var repo = new HashTypeRepository();
        repo.putCloseable(new A());
        repo.putCloseable(new B());
        repo.put(new C());
        return new PlainScopedServiceProvider(new ScopedTypeRepository(repo, new HashTypeRepository()));
    }

    @Benchmark
    public void benchClose() {
        PROVIDER.close();
    }

    @Benchmark
    public void benchScopedClose() {
        SCOPED_PROVIDER.close();
    }

    public static final class A implements Closeable {

        @Override
        public void close() {

        }
    }

    public static final class B implements Closeable {

        @Override
        public void close() {

        }
    }

    public static final class C {
    }
}
