package io.github.amayaframework.nodes;

import io.github.amayaframework.di.InjectPolicy;

public interface InjectNode<T> extends Node<T> {
    InjectPolicy getPolicy();

    Class<?> getType();

    String getValue();
}
