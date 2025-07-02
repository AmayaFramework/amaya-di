package io.github.amayaframework.di.schema;

import com.github.romanqed.jtype.Types;

import java.lang.reflect.*;

/**
 * A simple reflective implementation of the {@link TypeProcessor}. Ignores any annotations.
 */
public class ReflectionTypeProcessor implements TypeProcessor {

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

    @Override
    public Type process(Type type, AnnotatedElement element) {
        return process(type);
    }
}
