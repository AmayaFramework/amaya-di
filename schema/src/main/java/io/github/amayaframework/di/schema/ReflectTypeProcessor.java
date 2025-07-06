package io.github.amayaframework.di.schema;

import com.github.romanqed.jtype.Types;

import java.lang.reflect.*;
import java.util.Map;

/**
 * A basic implementation of {@link TypeProcessor} using reflection.
 * <p>
 * Normalizes primitive types to boxed equivalents and resolves wildcard bounds.
 */
public class ReflectTypeProcessor implements TypeProcessor {

    private static final Map<Class<?>, Class<?>> PRIMITIVES = Map.of(
            boolean.class, Boolean.class,
            char.class, Character.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            float.class, Float.class,
            long.class, Long.class,
            double.class, Double.class
    );

    private static Type process(Type type) {
        // process(Owner).RawType<process(T1), process(T2), ...>
        if (type instanceof ParameterizedType) {
            var parameterized = (ParameterizedType) type;
            var arguments = parameterized.getActualTypeArguments();
            var length = arguments.length;
            var parameters = new Type[length];
            for (var i = 0; i < length; ++i) {
                parameters[i] = process(arguments[i]);
            }
            return Types.ofOwned(
                    process(parameterized.getOwnerType()),
                    parameterized.getRawType(),
                    parameters
            );
        }
        // process(Type)[][][]...
        if (type instanceof GenericArrayType) {
            var array = (GenericArrayType) type;
            return Types.of(process(array.getGenericComponentType()));
        }
        // Turn wildcards to its upper bounds
        if (type instanceof WildcardType) {
            return process(((WildcardType) type).getUpperBounds()[0]);
        }
        return type;
    }

    /**
     * Process the given type (possibly generic, wildcard, array, primitive, etc.)
     * and return a version normalized for DI usage:
     * <ul>
     *     <li>Primitives boxed</li>
     *     <li>Type arguments recursively processed</li>
     *     <li>Wildcards replaced with upper bounds</li>
     *     <li>Generic arrays unwrapped to an element type</li>
     * </ul>
     *
     * @param type    non-null type to process
     * @param element the associated annotated element, for context
     * @return non-null, normalized {@link Type} for injection purposes
     */
    @Override
    public Type process(Type type, AnnotatedElement element) {
        if (type instanceof Class) {
            var clazz = (Class<?>) type;
            if (clazz.isPrimitive()) {
                return PRIMITIVES.get(clazz);
            }
            return clazz;
        }
        return process(type);
    }
}
