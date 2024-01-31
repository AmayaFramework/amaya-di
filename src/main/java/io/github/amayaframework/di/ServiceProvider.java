package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

public interface ServiceProvider {
    <T> Function0<T> get(Artifact artifact);

    <T> Function0<T> get(Class<T> type, Class<?>... generics);

    <T> Function0<T> get(Class<T> type);

    <T> T instantiate(Artifact artifact);

    <T> T instantiate(Class<T> type, Class<?>... generics);

    <T> T instantiate(Class<T> type);
}
