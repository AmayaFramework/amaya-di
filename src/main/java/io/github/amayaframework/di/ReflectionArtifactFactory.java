package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.di.scheme.IllegalTypeException;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * An {@link ArtifactFactory} implementation that uses reflection to parse types.
 */
public final class ReflectionArtifactFactory implements ArtifactFactory {
    private static final String ARRAY = "[";
    private static final String REFERENCE = "L";

    private static Class<?> of(Type type, int array) {
        if (!(type instanceof Class)) {
            throw new IllegalTypeException("It is not possible to use a statically non-removable type", type);
        }
        var clazz = (Class<?>) type;
        if (array == 0) {
            return clazz;
        }
        return Exceptions.suppress(() -> Class.forName(
                ARRAY.repeat(array) + REFERENCE + clazz.getTypeName() + ";",
                false,
                clazz.getClassLoader()
        ));
    }

    private static Type unpackWildcard(Type type) {
        if (!(type instanceof WildcardType)) {
            return type;
        }
        var wildcard = (WildcardType) type;
        if (wildcard.getLowerBounds().length != 0) {
            throw new IllegalTypeException("Super wildcards are not supported", type);
        }
        var bounds = wildcard.getUpperBounds();
        if (bounds.length != 1) {
            throw new IllegalTypeException("Multiple wildcards are not supported", type);
        }
        return bounds[0];
    }

    private static Object process(Type type) {
        type = unpackWildcard(type);
        var array = 0;
        while (type instanceof GenericArrayType) {
            type = ((GenericArrayType) type).getGenericComponentType();
            ++array;
        }
        if (!(type instanceof ParameterizedType)) {
            return of(type, array);
        }
        var parameterized = (ParameterizedType) type;
        var clazz = of(parameterized.getRawType(), array);
        var arguments = parameterized.getActualTypeArguments();
        var metadata = new Object[arguments.length];
        var wildcards = 0;
        for (var i = 0; i < arguments.length; ++i) {
            var object = process(arguments[i]);
            if (object == Object.class) {
                ++wildcards;
            }
            metadata[i] = object;
        }
        if (metadata.length == wildcards) {
            return clazz;
        }
        return new Artifact(clazz, metadata);
    }

    @Override
    public Artifact create(Type type) {
        Objects.requireNonNull(type);
        if (type instanceof Class<?> && ((Class<?>) type).isPrimitive()) {
            throw new IllegalTypeException("Primitive types are not supported", type);
        }
        var ret = process(type);
        if (ret instanceof Artifact) {
            return (Artifact) ret;
        }
        return new Artifact((Class<?>) ret);
    }
}
