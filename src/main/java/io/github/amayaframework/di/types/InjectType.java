package io.github.amayaframework.di.types;

import java.util.Collection;

public interface InjectType {
    Collection<InjectField> getFields();

    Collection<InjectMethod> getMethods();

    InjectConstructor getConstructor();

    Class<?> getTarget();
}
