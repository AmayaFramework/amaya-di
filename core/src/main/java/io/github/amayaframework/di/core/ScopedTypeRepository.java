package io.github.amayaframework.di.core;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ScopedTypeRepository implements TypeRepository {
    private final TypeRepository current;
    private final TypeRepository parent;

    public ScopedTypeRepository(TypeRepository current, TypeRepository parent) {
        this.current = current;
        this.parent = parent;
    }

    @Override
    public ObjectFactory get(Type type) {
        var ret = current.get(type);
        if (ret != null) {
            return ret;
        }
        return parent.get(type);
    }

    @Override
    public boolean canProvide(Type type) {
        return current.canProvide(type) || parent.canProvide(type);
    }

    @Override
    public void set(Type type, ObjectFactory factory) {
        current.set(type, factory);
    }

    @Override
    public ObjectFactory remove(Type type) {
        return current.remove(type);
    }

    @Override
    public void clear() {
        current.clear();
    }

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

    @Override
    public Iterator<Type> iterator() {
        return new ScopedIterator(current.iterator(), parent.iterator());
    }

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
