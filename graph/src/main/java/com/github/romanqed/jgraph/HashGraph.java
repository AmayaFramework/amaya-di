package com.github.romanqed.jgraph;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * {@link Graph} implementation using {@link Map} and {@link Set}. It is not thread-safe.
 *
 * @param <E> type of nodes
 */
public class HashGraph<E> implements Graph<E> {
    private final Map<E, Set<E>> body;

    /**
     * Constructs an empty graph.
     */
    public HashGraph() {
        this.body = new HashMap<>();
    }

    @Override
    public boolean addEdge(E from, E to) {
        if (!body.computeIfAbsent(from, t -> new HashSet<>()).add(to)) {
            return false;
        }
        if (body.containsKey(to)) {
            return false;
        }
        body.put(to, null);
        return true;
    }

    @Override
    public boolean removeEdge(E from, E to) {
        var nodes = body.get(from);
        if (nodes == null) {
            return false;
        }
        return nodes.remove(to);
    }

    @Override
    public boolean containsEdge(E from, E to) {
        var nodes = body.get(from);
        if (nodes == null) {
            return false;
        }
        return nodes.contains(to);
    }

    @Override
    public Set<E> getAdjacentNodes(E node) {
        return body.get(node);
    }

    @Override
    public void forEach(BiConsumer<E, E> consumer) {
        for (var entry : body.entrySet()) {
            var adjacents = entry.getValue();
            if (adjacents == null) {
                continue;
            }
            for (var adjacent : adjacents) {
                consumer.accept(entry.getKey(), adjacent);
            }
        }
    }

    @Override
    public int size() {
        return body.size();
    }

    @Override
    public boolean isEmpty() {
        return body.isEmpty();
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object o) {
        return body.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return body.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return body.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return body.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (body.containsKey(e)) {
            return false;
        }
        body.put(e, null);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!body.keySet().remove(o)) {
            return false;
        }
        for (var set : body.values()) {
            if (set == null) {
                continue;
            }
            set.remove(o);
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return body.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        var ret = false;
        for (var e : c) {
            ret = ret || this.add(e);
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        var ret = false;
        for (var e : c) {
            ret = ret || this.remove(e);
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        var ret = false;
        for (var node : body.keySet()) {
            if (c.contains(node)) {
                continue;
            }
            body.remove(node);
            ret = true;
        }
        return ret;
    }

    @Override
    public void clear() {
        this.body.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var graph = (HashGraph<?>) o;
        return body.equals(graph.body);
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }

    @Override
    public String toString() {
        var ret = new StringBuilder();
        forEach((from, to) -> ret.append(from).append("->").append(to).append(", "));
        var length = ret.length();
        if (length != 0) {
            ret.replace(length - 2, length, "");
        }
        return "Graph{" + body.keySet() + "; " + ret + "}";
    }
}
