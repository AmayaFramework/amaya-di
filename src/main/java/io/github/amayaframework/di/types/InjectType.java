package io.github.amayaframework.di.types;

import java.util.Collection;
import java.util.Objects;

public class InjectType {
    private final Class<?> target;
    private final Collection<InjectField> fields;
    private final Collection<InjectMethod> methods;
    private final InjectConstructor constructor;

    public InjectType(Class<?> target,
                      Collection<InjectField> fields,
                      Collection<InjectMethod> methods,
                      InjectConstructor constructor) {
        this.target = target;
        this.fields = fields;
        this.methods = methods;
        this.constructor = constructor;
    }

    public Class<?> getTarget() {
        return target;
    }

    public Collection<InjectField> getFields() {
        return fields;
    }

    public Collection<InjectMethod> getMethods() {
        return methods;
    }

    public InjectConstructor getConstructor() {
        return constructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InjectType)) {
            return false;
        }
        InjectType that = (InjectType) o;
        return Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
