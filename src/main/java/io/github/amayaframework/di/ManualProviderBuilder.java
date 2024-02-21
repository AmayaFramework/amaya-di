package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;

/**
 * An interface describing an abstract {@link ServiceProvider} builder with
 * the ability to manually build dependencies.
 */
public interface ManualProviderBuilder extends ServiceProviderBuilder {

    /**
     * Adds an artifact implementation provided by the specified function.
     * <br>
     * IMPORTANT: the result of a function call is not checked,
     * if a critical situation occurs during its operation, it will not be processed in any way
     * (i.e., the function itself must control the absence of artifacts, cyclic dependencies, etc.).
     * <br>
     * Use this function only if you know what you are doing!
     *
     * @param artifact the specified artifact, must be non-null
     * @param function the specified function, must be non-null
     * @return this {@link ManualProviderBuilder} instance
     */
    ManualProviderBuilder addManual(Artifact artifact, Function1<ArtifactProvider, Function0<?>> function);

    /**
     * Adds a type implementation provided by the specified function.
     * <br>
     * IMPORTANT: the result of a function call is not checked,
     * if a critical situation occurs during its operation, it will not be processed in any way
     * (i.e., the function itself must control the absence of artifacts, cyclic dependencies, etc.).
     * <br>
     * Use this function only if you know what you are doing!
     *
     * @param type     the specified type, must be non-null
     * @param function the specified function, must be non-null
     * @param <T>      the service type
     * @return this {@link ManualProviderBuilder} instance
     */
    <T> ManualProviderBuilder addManual(Class<T> type, Function1<ArtifactProvider, Function0<T>> function);

    // Override parent methods to provide proper flow api
    @Override
    ManualProviderBuilder setRepository(Repository repository);

    @Override
    <T> ManualProviderBuilder addService(Artifact artifact,
                                         Class<? extends T> implementation,
                                         Function1<Function0<T>, Function0<T>> wrapper);

    @Override
    ManualProviderBuilder addSingleton(Artifact artifact, Class<?> implementation);

    @Override
    ManualProviderBuilder addTransient(Artifact artifact, Class<?> implementation);

    @Override
    <T> ManualProviderBuilder addService(Class<T> type,
                                         Class<? extends T> implementation,
                                         Function1<Function0<T>, Function0<T>> wrapper);

    @Override
    <T> ManualProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation);

    @Override
    <T> ManualProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation);

    @Override
    <T> ManualProviderBuilder addService(Class<T> type, Function1<Function0<T>, Function0<T>> wrapper);

    @Override
    ManualProviderBuilder addSingleton(Class<?> type);

    @Override
    ManualProviderBuilder addTransient(Class<?> type);

    @Override
    ManualProviderBuilder addService(Artifact artifact, Function0<?> supplier);

    @Override
    <T> ManualProviderBuilder addService(Class<T> type, Function0<T> supplier);

    @Override
    ManualProviderBuilder removeService(Artifact artifact);

    @Override
    ManualProviderBuilder removeService(Class<?> type);
}
