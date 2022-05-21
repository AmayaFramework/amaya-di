package io.github.amayaframework.nodes;

import java.lang.reflect.Field;

public final class FieldNode extends AbstractInjectNode<Field> {
    public FieldNode(Field body) {
        super(body);
        this.type = body.getType();
    }
}
