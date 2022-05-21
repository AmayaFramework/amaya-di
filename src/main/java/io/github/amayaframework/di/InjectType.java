package io.github.amayaframework.di;

import io.github.amayaframework.nodes.ConstructorNode;
import io.github.amayaframework.nodes.FieldNode;
import io.github.amayaframework.nodes.MethodNode;
import io.github.amayaframework.nodes.Node;

import java.util.List;

public interface InjectType extends Node<Class<?>> {
    boolean isDirect();

    List<FieldNode> getFields();

    List<MethodNode> getMethods();

    List<ConstructorNode> getConstructors();
}
