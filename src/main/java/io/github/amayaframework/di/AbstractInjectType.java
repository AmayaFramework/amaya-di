package io.github.amayaframework.di;

import io.github.amayaframework.nodes.AbstractNode;

public abstract class AbstractInjectType extends AbstractNode<Class<?>> implements InjectType {
    private static final String TO_STRING = "InjectType {body=%s, methods=%s, fields=%s, constructors=%s}";

    public AbstractInjectType(Class<?> body) {
        super(body);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING, getBody().getSimpleName(), getMethods(), getFields(), getConstructors());
    }
}
