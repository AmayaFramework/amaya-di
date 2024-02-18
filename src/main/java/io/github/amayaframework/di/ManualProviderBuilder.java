package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.ReflectionSchemeFactory;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.BytecodeStubFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManualProviderBuilder extends CheckedProviderBuilder {
    protected Map<Artifact, Function1<ArtifactProvider, Function0<?>>> manual;

    /**
     * Constructs {@link ManualProviderBuilder} instance with the specified scheme and stub factories.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     */
    public ManualProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        super(schemeFactory, stubFactory);
    }

    /**
     * Creates {@link ManualProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using the specified annotation as marker.
     *
     * @param annotation the specified annotation, must be non-null
     * @return {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder create(Class<? extends Annotation> annotation) {
        return new ManualProviderBuilder(new ReflectionSchemeFactory(annotation), new BytecodeStubFactory());
    }

    /**
     * Creates {@link ManualProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using {@link Inject} annotation as marker.
     *
     * @return {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder create() {
        return create(Inject.class);
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

    public ManualProviderBuilder addManual(Artifact artifact, Function1<ArtifactProvider, Function0<?>> builder) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(builder);
        manual.put(artifact, builder);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> ManualProviderBuilder addManual(Class<T> type, Function1<ArtifactProvider, Function0<T>> builder) {
        return addManual(new Artifact(type), (Function1<ArtifactProvider, Function0<?>>) (Function1<?, ?>) builder);
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
        return this;
    }

    @Override
    public ManualProviderBuilder removeService(Class<?> type) {
        super.removeService(type);
        return this;
    }
}
