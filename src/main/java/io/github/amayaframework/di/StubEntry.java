package io.github.amayaframework.di;

import io.github.amayaframework.di.stub.CachedObjectFactory;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Represents a cached stub and the set of service types it provides within the current build context.
 * <p>
 * {@code StubEntry} is used during the container build phase to track which types are associated with
 * a particular {@link CachedObjectFactory} stub. This is essential for delayed injection wiring:
 * once all services are registered, these stubs are finalized by assigning the correct dependencies.
 * <p>
 * This class is internal to the builder implementation and is not intended for public use.
 *
 * @see CachedObjectFactory#set(Type, io.github.amayaframework.di.core.ObjectFactory)
 */
public final class StubEntry {
    /**
     * A set of types that this stub provides. These are used as lookup keys for dependency resolution.
     */
    public final Set<Type> types;

    /**
     * A cached stub factory that will later receive its actual dependencies.
     */
    public final CachedObjectFactory stub;

    /**
     * Constructs a new {@code StubEntry} with the given set of types and cached stub.
     *
     * @param types the types provided by the stub
     * @param stub  the {@link CachedObjectFactory} that will later be wired with its dependencies
     */
    public StubEntry(Set<Type> types, CachedObjectFactory stub) {
        this.types = types;
        this.stub = stub;
    }
}
