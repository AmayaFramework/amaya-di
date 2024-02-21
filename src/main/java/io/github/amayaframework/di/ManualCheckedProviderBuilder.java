package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link ManualProviderBuilder} implementation based on {@link CheckedProviderBuilder}.
 */
public class ManualCheckedProviderBuilder extends CheckedProviderBuilder implements ManualProviderBuilder {
    protected Map<Artifact, Function1<ArtifactProvider, Function0<?>>> manual;

    /**
     * Constructs {@link ManualCheckedProviderBuilder} instance with the specified scheme and stub factories.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     */
    public ManualCheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        super(schemeFactory, stubFactory);
    }

    @Override
    protected void reset() {
        super.reset();
        this.manual = new HashMap<>();
    }

    @Override
    protected boolean resolve(Artifact artifact) {
        return manual.containsKey(artifact) || super.resolve(artifact);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildArtifacts(Map<Class<?>, ClassScheme> schemes, LazyProvider provider) {
        super.buildArtifacts(schemes, provider);
        for (var entry : manual.entrySet()) {
            var artifact = entry.getKey();
            var builder = entry.getValue();
            provider.add(artifact, () -> (Function0<Object>) builder.invoke(provider));
        }
    }

    @Override
    public ManualProviderBuilder addManual(Artifact artifact,
                                           Function1<ArtifactProvider, Function0<?>> function) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(function);
        any.remove(artifact);
        strong.remove(artifact);
        manual.put(artifact, function);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ManualProviderBuilder addManual(Class<T> type,
                                               Function1<ArtifactProvider, Function0<T>> function) {
        return addManual(new Artifact(type), (Function1<ArtifactProvider, Function0<?>>) (Function1<?, ?>) function);
    }

    // Override parent methods to provide proper flow api
    @Override
    public ManualProviderBuilder setRepository(Repository repository) {
        super.setRepository(repository);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Artifact artifact,
                                                Class<? extends T> implementation,
                                                Function1<Function0<T>, Function0<T>> wrapper) {
        super.addService(artifact, implementation, wrapper);
        manual.remove(artifact);
        return this;
    }

    @Override
    public ManualProviderBuilder addSingleton(Artifact artifact, Class<?> implementation) {
        super.addSingleton(artifact, implementation);
        return this;
    }

    @Override
    public ManualProviderBuilder addTransient(Artifact artifact, Class<?> implementation) {
        super.addTransient(artifact, implementation);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Class<T> type,
                                                Class<? extends T> implementation,
                                                Function1<Function0<T>, Function0<T>> wrapper) {
        super.addService(type, implementation, wrapper);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation) {
        super.addSingleton(type, implementation);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation) {
        super.addTransient(type, implementation);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Class<T> type, Function1<Function0<T>, Function0<T>> wrapper) {
        super.addService(type, wrapper);
        return this;
    }

    @Override
    public ManualProviderBuilder addSingleton(Class<?> type) {
        super.addSingleton(type);
        return this;
    }

    @Override
    public ManualProviderBuilder addTransient(Class<?> type) {
        super.addTransient(type);
        return this;
    }

    @Override
    public ManualProviderBuilder addService(Artifact artifact, Function0<?> supplier) {
        super.addService(artifact, supplier);
        manual.remove(artifact);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Class<T> type, Function0<T> supplier) {
        super.addService(type, supplier);
        return this;
    }

    @Override
    public ManualProviderBuilder removeService(Artifact artifact) {
        super.removeService(artifact);
        manual.remove(artifact);
        return this;
    }

    @Override
    public ManualProviderBuilder removeService(Class<?> type) {
        super.removeService(type);
        return this;
    }
}
