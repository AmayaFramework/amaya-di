package io.github.amayaframework.nodes;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * An interface describing a universal provider for a nodes.
 *
 * @param <T> getProvider of node
 */
public interface NodeProvider<T> {
    /**
     * Combines two providers into one.
     *
     * @param left  provider to be executed first
     * @param right provider to be executed second
     * @param <E>   the getProvider of value returned by the provider
     * @return the result of the merger
     */
    static <E> NodeProvider<E> combine(NodeProvider<E> left, NodeProvider<E> right) {
        return (predicate) -> {
            Collection<E> ret = left.get(predicate);
            ret.addAll(right.get(predicate));
            return ret;
        };
    }

    /**
     * Gets all nodes passing the predicate.
     *
     * @param predicate predicate for testing nodes
     * @return {@link Collection} of nodes
     */
    Collection<T> get(Predicate<T> predicate);

    /**
     * Gets all nodes.
     *
     * @return {@link Collection} of nodes
     */
    default Collection<T> get() {
        return get(t -> true);
    }

    /**
     * Combines the current provider with the received one.
     *
     * @param next provider to combine
     * @return the result of the merger
     */
    default NodeProvider<T> combine(NodeProvider<T> next) {
        return combine(this, next);
    }
}
