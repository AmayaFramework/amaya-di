package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class RepositoryImpl implements Repository {
    private final Map<Type, Function0<Object>> body;

    RepositoryImpl() {
        this.body = new HashMap<>();
    }

    @Override
    public Function0<Object> get(Type type) {
        return body.get(type);
    }

    @Override
    public Iterable<Type> getAll() {
        return Collections.unmodifiableCollection(body.keySet());
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
}
