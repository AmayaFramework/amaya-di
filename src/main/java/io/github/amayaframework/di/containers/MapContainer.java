package io.github.amayaframework.di.containers;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MapContainer implements Container {
    private final Map<Integer, Object> body;

    public MapContainer(Map<Integer, Object> body) {
        this.body = Objects.requireNonNull(body);
    }

    public MapContainer() {
        this.body = new ConcurrentHashMap<>();
    }

    @Override
    public Object get(int key) {
        return body.get(key);
    }

    @Override
    public Object put(int key, Object value) {
        return body.put(key, value);
    }

    @Override
    public Object remove(int key) {
        return body.remove(key);
    }
}
