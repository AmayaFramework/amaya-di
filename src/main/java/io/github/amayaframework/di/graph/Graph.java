package io.github.amayaframework.di.graph;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * An interface describing an oriented unweighted graph.
 * Implements {@link Collection} which describes a collection of graph nodes.
 * Changing the nodes will result in corresponding changes in the edges, and vice versa.
 * <br>
 * For example,
 * <br>
 * 1) deleting nodes A and/or B will delete edge A-{@literal >}B,
 * <br>
 * 2) adding an edge A-{@literal >}B will add nodes A and B.
 *
 * @param <E> type of nodes
 */
public interface Graph<E> extends Collection<E> {

    /**
     * Adds an edge leading from first node to second node.
     *
     * @param from the source node
     * @param to   the destination node
     * @return true if the graph has changed, false otherwise
     */
    boolean addEdge(E from, E to);

    /**
     * Removes an edge leading from first node to second node.
     *
     * @param from the source node
     * @param to   the destination node
     * @return true if the graph has changed, false otherwise
     */
    boolean removeEdge(E from, E to);

    /**
     * Checks whether the graph contains an edge leading from first node to second node.
     *
     * @param from the source node
     * @param to   the destination node
     * @return true if such an edge exists, false otherwise
     */
    boolean containsEdge(E from, E to);

    /**
     * Returns a set of nodes that are adjacent to the specified one.
     *
     * @param node specified node
     * @return null or an empty set if there are no adjacent notes, otherwise a set containing adjacent nodes
     */
    Set<E> getAdjacentNodes(E node);

    /**
     * Performs the specified action for each edge contained in the graph.
     *
     * @param consumer specified action
     */
    void forEach(BiConsumer<E, E> consumer);

    /**
     * Calculates the hash code based on the contents of the graph.
     *
     * @return calculated value
     */
    int hashCode();

    /**
     * Indicates whether some other object is "equal to" this one.
     * Performs a "deep" equal check, that is, only those with
     * identical structure will be recognized as equal graphs.
     *
     * @param o object to be compared for equality with this collection
     * @return the result of the equal check
     */
    boolean equals(Object o);
}
