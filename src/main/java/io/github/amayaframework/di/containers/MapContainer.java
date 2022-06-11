package io.github.amayaframework.di.containers;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link Container} implementation using {@link ConcurrentHashMap}.
 */
public class MapContainer implements Container {
    private final Map<Integer, Object> body;

    public MapContainer(Map<Integer, Object> body) {
        this.body = Objects.requireNonNull(body);
    }

    public MapContainer() {
        this.body = new ConcurrentHashMap<>();
    }

    @Override
    public Object get(Integer key) {
        return body.get(key);
    }

    @Override
    public Object put(Integer key, Object value) {
        return body.put(key, value);
    }

    @Override
    public Object remove(Integer key) {
        return body.remove(key);
    }
}
