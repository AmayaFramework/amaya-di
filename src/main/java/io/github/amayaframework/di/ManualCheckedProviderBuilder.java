package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.StubFactory;
import io.github.amayaframework.di.stub.TypeProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link ManualProviderBuilder} implementation based on {@link CheckedProviderBuilder}.
 */
public class ManualCheckedProviderBuilder extends CheckedProviderBuilder implements ManualProviderBuilder {
    protected Map<Type, Function1<TypeProvider, Function0<?>>> manual;

    /**
     * Constructs {@link ManualCheckedProviderBuilder} instance with the specified scheme, stub factories and check set.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @param checks        the specified set of applied checks
     */
    public ManualCheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory, int checks) {
        super(schemeFactory, stubFactory, checks);
    }

    /**
     * Constructs {@link ManualCheckedProviderBuilder} instance with the specified scheme and stub factories.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     */
    public ManualCheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        this(schemeFactory, stubFactory, VALIDATE_ALL);
    }

    @Override
    protected void reset() {
        super.reset();
        this.manual = new HashMap<>();
    }

    @Override
    protected boolean canResolve(Type type) {
        return manual.containsKey(type) || super.canResolve(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildTypes(Map<Class<?>, ClassScheme> schemes, LazyProvider provider) {
        super.buildTypes(schemes, provider);
        for (var entry : manual.entrySet()) {
            var type = entry.getKey();
            var builder = entry.getValue();
            provider.add(type, () -> (Function0<Object>) builder.invoke(provider));
        }
    }

    @Override
    public ManualProviderBuilder addManual(Type type,
                                           Function1<TypeProvider, Function0<?>> function) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(function);
        any.remove(type);
        strong.remove(type);
        manual.put(type, function);
        return this;
    }

    // Override parent methods to provide proper flow api
    @Override
    public ManualProviderBuilder setRepository(ServiceRepository repository) {
        super.setRepository(repository);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Type type,
                                                Class<? extends T> implementation,
                                                ServiceWrapper<T> wrapper) {
        super.addService(type, implementation, wrapper);
        manual.remove(type);
        return this;
    }

    @Override
    public ManualProviderBuilder addSingleton(Type type, Class<?> implementation) {
        super.addSingleton(type, implementation);
        return this;
    }

    @Override
    public ManualProviderBuilder addTransient(Type type, Class<?> implementation) {
        super.addTransient(type, implementation);
        return this;
    }

    @Override
    public <T> ManualProviderBuilder addService(Class<T> type,
                                                Class<? extends T> implementation,
                                                ServiceWrapper<T> wrapper) {
        super.addService(type, implementation, wrapper);
        manual.remove(type);
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
    public <T> ManualProviderBuilder addService(Class<T> type, ServiceWrapper<T> wrapper) {
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
    public ManualProviderBuilder addService(Type type, Function0<?> supplier) {
        super.addService(type, supplier);
        manual.remove(type);
        return this;
    }

    @Override
    public ManualProviderBuilder removeService(Type type) {
        super.removeService(type);
        manual.remove(type);
        return this;
    }
}
