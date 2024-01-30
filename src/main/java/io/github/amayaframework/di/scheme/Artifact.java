package io.github.amayaframework.di.scheme;

import java.util.Arrays;
import java.util.Objects;

public final class Artifact {
    private final Class<?> type;
    private final Object[] metadata;

    public Artifact(Class<?> type, Object[] metadata) {
        this.type = Objects.requireNonNull(type);
        this.metadata = metadata;
    }

    public Artifact(Class<?> type, Object metadata) {
        this(type, new Object[]{metadata});
    }

    public Artifact(Class<?> type) {
        this.type = Objects.requireNonNull(type);
        this.metadata = null;
    }

    public static Artifact of(Class<?> type, Object... metadata) {
        return new Artifact(type, metadata);
    }

    public Class<?> getType() {
        return type;
    }

    public Object[] getMetadata() {
        return metadata == null ? null : metadata.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var artifact = (Artifact) o;
        return type.equals(artifact.type) && Arrays.equals(metadata, artifact.metadata);
    }

    @Override
    public int hashCode() {
        if (metadata == null) {
            return type.hashCode();
        }
        var ret = 31 + type.hashCode();
        for (var element : metadata) {
            ret = 31 * ret + (element == null ? 0 : element.hashCode());
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "type=" + type +
                ", metadata=" + Arrays.toString(metadata) +
                '}';
    }
}
