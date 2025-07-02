package io.github.amayaframework.di.core;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A {@link TypeRepository} implementation that supports scoped resolution by delegating to
 * a parent repository when a type is not found in the current scope.
 * <br>
 * All modifications affect only the current (scoped) repository.
 */
public final class ScopedTypeRepository implements TypeRepository {
    private final TypeRepository current;
    private final TypeRepository parent;

    /**
     * Constructs a new scoped repository using the specified current and parent repositories.
     *
     * @param current the local (scoped) repository, must be non-null
     * @param parent  the parent repository used for fallback resolution, must be non-null
     */
    public ScopedTypeRepository(TypeRepository current, TypeRepository parent) {
        this.current = current;
        this.parent = parent;
    }

    /**
     * Gets the object factory for the specified type.
     * <br>
     * First attempts to resolve the type from the current (scoped) repository.
     * If not found, delegates to the parent repository.
     *
     * @param type the type to resolve, must be non-null
     * @return the associated {@link ObjectFactory}, or null if not found in either repository
     */
    @Override
    public ObjectFactory get(Type type) {
        var ret = current.get(type);
        if (ret != null) {
            return ret;
        }
        return parent.get(type);
    }

    /**
     * Checks whether the specified type can be resolved by this repository.
     * <br>
     * Returns true if either the current or parent repository contains a factory for the type.
     *
     * @param type the type to check, must be non-null
     * @return true if a factory exists for the type, false otherwise
     */
    @Override
    public boolean canProvide(Type type) {
        return current.canProvide(type) || parent.canProvide(type);
    }

    /**
     * Sets a factory for the specified type in the current (scoped) repository.
     *
     * @param type    the type to associate with the factory, must be non-null
     * @param factory the factory to associate, must be non-null
     */
    @Override
    public void set(Type type, ObjectFactory factory) {
        current.set(type, factory);
    }

    /**
     * Removes the factory for the specified type from the current (scoped) repository only.
     *
     * @param type the type whose factory should be removed, must be non-null
     * @return the removed {@link ObjectFactory}, or null if not found
     */
    @Override
    public ObjectFactory remove(Type type) {
        return current.remove(type);
    }

    /**
     * Removes all type-factory associations from the current (scoped) repository.
     * <br>
     * Does not affect the parent repository.
     */
    @Override
    public void clear() {
        current.clear();
    }

    /**
     * Iterates over all unique type-factory pairs from both the current and parent repositories.
     * <br>
     * If a type exists in both, only the current repository's version is passed to the consumer.
     *
     * @param action the action to perform on each type-factory pair
     */
    @Override
    public void forEach(BiConsumer<Type, ObjectFactory> action) {
        var visited = new HashSet<Type>();
        current.forEach((type, factory) -> {
            visited.add(type);
            action.accept(type, factory);
        });
        parent.forEach((type, factory) -> {
            if (!visited.contains(type)) {
                action.accept(type, factory);
            }
        });
    }

    /**
     * Returns an iterator over all unique types available in this repository.
     * <br>
     * Types from the current repository override those from the parent repository.
     *
     * @return an iterator over the available types
     */
    @Override
    public Iterator<Type> iterator() {
        return new ScopedIterator(current.iterator(), parent.iterator());
    }

    /**
     * Performs the given action for each unique type available in this repository.
     * <br>
     * Types from the current repository override those from the parent repository.
     *
     * @param action the action to perform on each type
     */
    @Override
    public void forEach(Consumer<? super Type> action) {
        var visited = new HashSet<Type>();
        current.forEach(type -> {
            visited.add(type);
            action.accept(type);
        });
        parent.forEach(type -> {
            if (!visited.contains(type)) {
                action.accept(type);
            }
        });
    }

    private static final class ScopedIterator implements Iterator<Type> {
        final Set<Type> visited;
        Iterator<Type> current;
        Iterator<Type> parent;
        Type last;

        private ScopedIterator(Iterator<Type> current, Iterator<Type> parent) {
            this.current = current;
            this.parent = parent;
            this.last = null;
            this.visited = new HashSet<>();
        }

        @Override
        public boolean hasNext() {
            if (current != null && current.hasNext()) {
                return true;
            }
            current = null;
            if (!parent.hasNext()) {
                return false;
            }
            var found = parent.next();
            while (visited.contains(found)) {
                if (!parent.hasNext()) {
                    return false;
                }
                found = parent.next();
            }
            last = found;
            return true;
        }

        @Override
        public Type next() {
            if (last != null) {
                var ret = last;
                last = null;
                return ret;
            }
            if (current != null && current.hasNext()) {
                var ret = current.next();
                visited.add(ret);
                return ret;
            }
            current = null;
            var found = parent.next();
            while (visited.contains(found)) {
                found = parent.next();
            }
            return found;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException("Cannot remove types from parent repository");
            }
            current.remove();
        }
    }
}
