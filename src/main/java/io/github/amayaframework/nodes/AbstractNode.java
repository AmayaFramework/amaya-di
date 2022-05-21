package io.github.amayaframework.nodes;

import java.util.Objects;

public abstract class AbstractNode<T> implements Node<T> {
    private final T body;

    public AbstractNode(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        return Objects.equals(body, ((AbstractNode<?>) obj).body);
    }
}
