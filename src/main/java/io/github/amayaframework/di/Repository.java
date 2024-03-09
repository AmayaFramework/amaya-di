package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

/**
 * An interface describing an abstract repository of instantiators associated with the specified artifacts.
 */
public interface Repository {

    /**
     * Gets the instantiator associated with the specified artifact.
     * <br>
     * Important:
     * if null was returned, it definitely means that the instantiator was not found,
     * but the opposite is NOT TRUE.
     * <br>
     * For example,
     * <pre>
     * var func = repository.get(Artifact.of(String.class));
     * System.out.println(func.invoke()); =&gt; "null"
     * </pre>
     * Here func is not null, but will always return null.
     * <br>
     * So, to unambiguously determine whether the repository contains an artifact or not,
     * use {@link Repository#contains(Artifact)}.
     *
     * @param artifact the specified artifact, must be non-null
     * @return null or {@link Function0} instance
     */
    Function0<Object> get(Artifact artifact);

    /**
     * Returns all artifacts currently stored in the repository.
     *
     * @return {@link Iterable} instance contains stored artifacts
     */
    Iterable<Artifact> getAll();

    /**
     * Checks whether the repository contains an instantiator for the specified artifact.
     *
     * @param artifact the specified artifact, must be non-null
     * @return true, if contains, false otherwise
     */
    boolean contains(Artifact artifact);

    /**
     * Adds an instantiator associated with the specified artifact, overwriting the previous one.
     *
     * @param artifact the specified artifact, must be non-null
     * @param supplier the specified instantiator, must be non-null
     */
    void add(Artifact artifact, Function0<Object> supplier);

    /**
     * Removes the instantiator associated with the specified artifact.
     *
     * @param artifact the specified artifact, must be non-null
     * @return true if the instantiator was removed, false otherwise
     */
    boolean remove(Artifact artifact);

    /**
     * Clears this repository.
     */
    void clear();
}
