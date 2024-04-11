package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

/**
 * An interface describing an abstract repository of instantiators associated with the specified types.
 */
public interface Repository extends Iterable<Type> {

    //    /**
//     * Gets the instantiator associated with the specified type.
//     * <br>
//     * Important:
//     * if null was returned, it definitely means that the instantiator was not found,
//     * but the opposite is NOT TRUE.
//     * <br>
//     * For example,
//     * <pre>
//     * var func = repository.get(Artifact.of(String.class));
//     * System.out.println(func.invoke()); =&gt; "null"
//     * </pre>
//     * Here func is not null, but will always return null.
//     * <br>
//     * So, to unambiguously determine whether the repository contains an type or not,
//     * use {@link Repository#contains(Artifact)}.
//     *
//     * @param type the specified type, must be non-null
//     * @return null or {@link Function0} instance
//     */
    Function0<Object> get(Type type);

    //    /**
//     * Checks whether the repository contains an instantiator for the specified type.
//     *
//     * @param type the specified type, must be non-null
//     * @return true, if contains, false otherwise
//     */
    boolean contains(Type type);

    //    /**
//     * Adds an instantiator associated with the specified type, overwriting the previous one.
//     *
//     * @param type the specified type, must be non-null
//     * @param supplier the specified instantiator, must be non-null
//     */
    void add(Type type, Function0<Object> supplier);

    //    /**
//     * Removes the instantiator associated with the specified type.
//     *
//     * @param type the specified type, must be non-null
//     * @return true if the instantiator was removed, false otherwise
//     */
    boolean remove(Type type);

    /**
     * Clears this repository.
     */
    void clear();

    void forEach(BiConsumer<Type, Function0<Object>> consumer);
}
