package io.github.amayaframework.di;

import io.github.amayaframework.di.stub.CachedObjectFactory;

import java.lang.reflect.Type;
import java.util.Set;

public final class StubEntry {
    public final Set<Type> types;
    public final CachedObjectFactory stub;

    public StubEntry(Set<Type> types, CachedObjectFactory stub) {
        this.types = types;
        this.stub = stub;
    }
}
