package io.github.amayaframework.di.types;

import io.github.amayaframework.di.annotations.InjectPolicy;
import org.objectweb.asm.Type;

import java.util.Objects;

public class InjectMember {
    private final Object body;
    private final Class<?> clazz;
    private final Type type;
    private final InjectPolicy policy;
    private final String value;

    public InjectMember(Object body, Class<?> clazz, InjectPolicy policy, String value) {
        this.body = body;
        this.clazz = clazz;
        this.type = Type.getType(clazz);
        this.policy = policy;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public InjectPolicy getPolicy() {
        return policy;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InjectMember)) {
            return false;
        }
        InjectMember that = (InjectMember) o;
        return body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
