package io.github.amayaframework.di.scheme;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

abstract class AbstractExecutableScheme<T extends Executable>
        extends AbstractScheme<T>
        implements ExecutableScheme<T> {
    protected final Set<Type> types;
    protected final Type[] mapping;

    protected AbstractExecutableScheme(T target, Set<Type> types, Type[] mapping) {
        super(target);
        this.types = Collections.unmodifiableSet(Objects.requireNonNull(types));
        this.mapping = Objects.requireNonNull(mapping);
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Type[] getMapping() {
        return mapping.clone();
    }
}
