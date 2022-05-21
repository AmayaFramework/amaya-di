package io.github.amayaframework.nodes;

import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.InjectPolicy;
import io.github.amayaframework.di.Value;

import java.lang.reflect.AnnotatedElement;

public abstract class AbstractInjectNode<T extends AnnotatedElement> extends AbstractNode<T> implements InjectNode<T> {
    private final InjectPolicy policy;
    private final String value;
    protected Class<?> type;

    public AbstractInjectNode(T body) {
        super(body);
        this.policy = body.getAnnotation(Inject.class).value();
        Value value = body.getAnnotation(Value.class);
        if (value == null) {
            this.value = null;
        } else {
            this.value = value.value();
        }
    }

    @Override
    public InjectPolicy getPolicy() {
        return policy;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getValue() {
        return value;
    }
}
