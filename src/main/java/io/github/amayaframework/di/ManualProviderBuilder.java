package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import io.github.amayaframework.di.stub.TypeProvider;

import java.lang.reflect.Type;

/**
 * An interface describing an abstract {@link ServiceProvider} builder with
 * the ability to manually build dependencies.
 */
public interface ManualProviderBuilder extends ServiceProviderBuilder {

    /**
     * Adds a type implementation provided by the specified function.
     * <br>
     * IMPORTANT: the result of a function call is not checked,
     * if a critical situation occurs during its operation, it will not be processed in any way
     * (i.e., the function itself must control the absence of types, cyclic dependencies, etc.).
     * <br>
     * Use this function only if you know what you are doing!
     *
     * @param type     the specified type, must be non-null
     * @param function the specified function, must be non-null
     * @return this {@link ManualProviderBuilder} instance
     */
    ManualProviderBuilder addManual(Type type, Function1<TypeProvider, Function0<?>> function);

    // Override parent methods to provide proper flow api
    @Override
    ManualProviderBuilder setRepository(Repository repository);

    @Override
    <T> ManualProviderBuilder addService(Type type, Class<? extends T> implementation, ServiceWrapper<T> wrapper);

    @Override
    ManualProviderBuilder addSingleton(Type type, Class<?> implementation);

    @Override
    ManualProviderBuilder addTransient(Type type, Class<?> implementation);

    @Override
    <T> ManualProviderBuilder addService(Class<T> type, Class<? extends T> implementation, ServiceWrapper<T> wrapper);

    @Override
    <T> ManualProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation);

    @Override
    <T> ManualProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation);

    @Override
    <T> ManualProviderBuilder addService(Class<T> type, ServiceWrapper<T> wrapper);

    @Override
    ManualProviderBuilder addSingleton(Class<?> type);

    @Override
    ManualProviderBuilder addTransient(Class<?> type);

    @Override
    ManualProviderBuilder addService(Type type, Function0<?> supplier);

    @Override
    ManualProviderBuilder removeService(Type type);
}
