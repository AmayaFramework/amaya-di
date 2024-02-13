package io.github.amayaframework.di;

import java.util.Arrays;
import java.util.Objects;

/**
 * The main entity necessary to formalize the task of injecting dependencies.
 * It is a universal descriptor for services containing a java type and some metadata array.
 * The need to use an {@link Artifact}, rather than an instance of a {@link Class},
 * is due to the need to specialize additional meta-information about types.
 * For example, in the case shown below, in order to unambiguously solve the dependency,
 * in addition to type information, it is necessary to save information about generics:
 * <pre>
 *     class AuthorizeService {
 *         public AuthorizeService(
 *          Database&lt;User&gt; users,
 *          Database&lt;Session&gt; sessions) {
 *             ...
 *         }
 *     }
 * </pre>
 * However, given the variety of existing approaches to organizing DI, only information about generics is not always
 * used, therefore, for universalization, metadata is presented as an ordered array of {@link Object} instances.
 * The {@link Artifact} can (and should) be used as a key in hash structures.
 * The implementation of {@link Artifact#hashCode()} and {@link Artifact#equals(Object)} takes into account
 * the order of metadata.
 */
public final class Artifact {
    private final Class<?> type;
    private final Object[] metadata;

    /**
     * Constructs artifact, containing the specified class and metadata array.
     *
     * @param type     the specified class, must be non-null
     * @param metadata the specified metadata, may be null
     */
    public Artifact(Class<?> type, Object[] metadata) {
        this.type = Objects.requireNonNull(type);
        this.metadata = metadata;
    }

    /**
     * Constructs artifact, containing the specified class and metadata object.
     *
     * @param type     the specified class, must be non-null
     * @param metadata the specified metadata, may be null
     */
    public Artifact(Class<?> type, Object metadata) {
        this(type, new Object[]{metadata});
    }

    /**
     * Constructs artifact, containing the specified class.
     *
     * @param type the specified class, must be non-null
     */
    public Artifact(Class<?> type) {
        this.type = Objects.requireNonNull(type);
        this.metadata = null;
    }

    /**
     * Creates artifact, containing the specified class and metadata objects. Varargs implementation.
     * The most preferred and safe way to create an artifact for the user.
     * Example of creating an artifact for a complex generic declaration:
     * <pre>
     *     Map&lt;List&lt;String&gt;, List&lt;Integer&gt;[]&gt; =&gt;
     *     Artifact.of(Map.class,
     *         Artifact.of(List.class, String.class),
     *         Artifact.of(List[].class, Integer.class)
     *     )
     * </pre>
     *
     * @param type     the specified class, must be non-null
     * @param metadata the specified metadata, may be null
     * @return {@link Artifact} instance
     */
    public static Artifact of(Class<?> type, Object... metadata) {
        return new Artifact(type, metadata);
    }

    /**
     * Creates artifact, containing the specified class.
     * The most preferred and safe way to create an artifact for the user.
     *
     * @param type the specified class, must be non-null
     * @return {@link Artifact} instance
     */
    public static Artifact of(Class<?> type) {
        return new Artifact(type);
    }

    /**
     * Returns artifact type.
     *
     * @return non-null {@link Class} instance
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Returns artifact metadata. Changing the resulting array will not change the artifact.
     *
     * @return null or {@link Object} array
     */
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
