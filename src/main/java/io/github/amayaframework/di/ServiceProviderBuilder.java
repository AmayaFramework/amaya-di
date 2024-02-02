package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;

public interface ServiceProviderBuilder {
    ServiceProviderBuilder setRepository(Repository repository);

    <T> ServiceProviderBuilder addService(Artifact artifact,
                                          Class<? extends T> implementation,
                                          Function1<Function0<T>, Function0<T>> wrapper);

    ServiceProviderBuilder addSingleton(Artifact artifact, Class<?> implementation);

    ServiceProviderBuilder addTransient(Artifact artifact, Class<?> implementation);

    <T> ServiceProviderBuilder addService(Class<T> type,
                                          Class<? extends T> implementation,
                                          Function1<Function0<T>, Function0<T>> wrapper);

    <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation);

    <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation);

    <T> ServiceProviderBuilder addService(Class<T> type, Function1<Function0<T>, Function0<T>> wrapper);

    ServiceProviderBuilder addSingleton(Class<?> type);

    ServiceProviderBuilder addTransient(Class<?> type);

    ServiceProviderBuilder addService(Artifact artifact, Function0<?> supplier);

    <T> ServiceProviderBuilder addService(Class<T> type, Function0<T> supplier);

    ServiceProviderBuilder removeService(Artifact artifact);

    ServiceProviderBuilder removeService(Class<?> type);

    ServiceProvider build();
}
