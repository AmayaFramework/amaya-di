package io.github.amayaframework.di;

import java.util.Objects;

public final class Artifact {
    private final Class<?> type;
    private final String name;

    public Artifact(Class<?> type, String name) {
        this.type = Objects.requireNonNull(type);
        this.name = name;
    }

    public Artifact(Class<?> type) {
        this(type, null);
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var artifact = (Artifact) o;
        return type.equals(artifact.type) && Objects.equals(name, artifact.name);
    }

    @Override
    public int hashCode() {
        return name == null ? type.hashCode() : Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
