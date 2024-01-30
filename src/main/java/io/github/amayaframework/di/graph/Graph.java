package io.github.amayaframework.di.graph;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

public interface Graph<E> extends Collection<E> {
    boolean addEdge(E from, E to);

    boolean removeEdge(E from, E to);

    boolean containsEdge(E from, E to);

    Set<E> getAdjacentNodes(E node);

    void forEach(BiConsumer<E, E> consumer);

    int hashCode();

    boolean equals(Object o);
}
