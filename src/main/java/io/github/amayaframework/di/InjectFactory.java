package io.github.amayaframework.di;

import io.github.amayaframework.nodes.NodeFactory;

public interface InjectFactory extends NodeFactory {
    InjectType getInjectType(Class<?> clazz);
}
