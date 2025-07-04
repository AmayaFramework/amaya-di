package io.github.amayaframework.di;

import io.github.amayaframework.di.stub.CachedObjectFactory;

import java.lang.reflect.Type;
import java.util.Set;

final class StubEntry {
    final Set<Type> types;
    final CachedObjectFactory stub;

    StubEntry(Set<Type> types, CachedObjectFactory stub) {
        this.types = types;
        this.stub = stub;
    }
}
