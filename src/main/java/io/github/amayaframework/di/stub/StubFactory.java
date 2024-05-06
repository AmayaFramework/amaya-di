package io.github.amayaframework.di.stub;

import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.scheme.ClassScheme;

/**
 * An interface describing an abstract factory that creates
 * instantiators based on the passed class scheme and dependency provider.
 */
public interface StubFactory {

    /**
     * Creates an instantiator for the specified class scheme using the specified provider.
     *
     * @param scheme   the specified scheme, must be non-null
     * @param provider the specified provider, must be non-null
     * @return the created instantiator
     */
    Function0<?> create(ClassScheme scheme, TypeProvider provider);
}
