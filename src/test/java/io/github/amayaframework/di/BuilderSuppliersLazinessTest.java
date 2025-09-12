package io.github.amayaframework.di;

import io.github.amayaframework.di.core.HashTypeRepository;
import io.github.amayaframework.di.core.ScopedRepository;
import io.github.amayaframework.di.core.ScopedTypeRepository;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.reflect.ReflectStubFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class BuilderSuppliersLazinessTest {

    @Test
    public void repositorySupplierIsCalledOnceOnBuild() {
        var calls = new AtomicInteger();
        Supplier<TypeRepository> repoSupplier = () -> {
            calls.incrementAndGet();
            return new HashTypeRepository();
        };

        var sp = ProviderBuilders.create(new ReflectStubFactory())
                .withRepository(repoSupplier)
                .addSingleton(Dummy.class)
                .build();

        assertNotNull(sp);
        assertEquals(1, calls.get());
    }

    @Test
    public void scopedRepositorySupplierIsCalledOnCreateScopedOnly() {
        var calls = new AtomicInteger();
        Supplier<ScopedRepository> scopedSupplier = () -> {
            calls.incrementAndGet();
            return new ScopedTypeRepository(new HashTypeRepository(), new HashTypeRepository());
        };

        var sp = ProviderBuilders.createScoped(new ReflectStubFactory())
                .withScopedRepository(scopedSupplier)
                .addScopedSingleton(Dummy.class)
                .build();

        assertEquals(0, calls.get());
        var scope = sp.createScoped();
        assertNotNull(scope);
        assertEquals(1, calls.get());
    }

    public static final class Dummy {
    }
}
