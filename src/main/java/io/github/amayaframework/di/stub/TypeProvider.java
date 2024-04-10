package io.github.amayaframework.di.stub;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.function.Function;

///**
// * An interface describing the read-only version of the repository - the artifact provider.
// * It is used inside the builder implementation when resolving artifacts.
// */
@FunctionalInterface
public interface TypeProvider extends Function<Type, Function0<Object>> {

    //    /**
//     * Gets the instantiator associated with the specified artifact.
//     *
//     * @param type the specified artifact
//     * @return null or {@link Function0} instance
//     */
    Function0<Object> apply(Type type);

    @SuppressWarnings("unchecked")
    default <T> Function0<T> apply(Class<T> type) {
        return (Function0<T>) this.apply((Type) type);
    }
}
