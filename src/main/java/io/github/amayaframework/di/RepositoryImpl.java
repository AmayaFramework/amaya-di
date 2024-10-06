package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

final class RepositoryImpl implements ServiceRepository {
    private final Map<Type, Function0<Object>> body;
    private final Set<Type> keys;

    RepositoryImpl() {
        this.body = new HashMap<>();
        this.keys = Collections.unmodifiableSet(this.body.keySet());
    }

    @Override
    public Function0<Object> get(Type type) {
        return body.get(type);
    }

    @Override
    public boolean contains(Type type) {
        return body.containsKey(type);
    }

    @Override
    public void add(Type type, Function0<Object> supplier) {
        body.put(type, supplier);
    }

    @Override
    public boolean remove(Type type) {
        return body.remove(type) != null;
    }

    @Override
    public void clear() {
        body.clear();
    }

    @Override
    public void forEach(Consumer<? super Type> action) {
        keys.forEach(action);
    }

    @Override
    public Spliterator<Type> spliterator() {
        return keys.spliterator();
    }

    @Override
    public void forEach(BiConsumer<Type, Function0<Object>> action) {
        body.forEach(action);
    }

    @Override
    public Iterator<Type> iterator() {
        return keys.iterator();
    }
}
