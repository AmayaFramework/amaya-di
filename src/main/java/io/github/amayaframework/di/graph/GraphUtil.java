package io.github.amayaframework.di.graph;

import java.util.*;

public final class GraphUtil {
    private GraphUtil() {
    }

    private static <E> List<Node<E>> graphToNodes(Graph<E> graph) {
        var ret = new LinkedList<Node<E>>();
        var map = new HashMap<E, Node<E>>();
        for (var node : graph) {
            var t = new Node<>(node);
            ret.add(t);
            map.put(node, t);
        }
        for (var node : ret) {
            var nodes = graph.getAdjacentNodes(node.value);
            if (nodes == null || nodes.isEmpty()) {
                node.adjacents = Collections.emptyList();
                continue;
            }
            var adjacents = new ArrayList<Node<E>>();
            nodes.forEach(e -> adjacents.add(map.get(e)));
            node.adjacents = adjacents;
        }
        return ret;
    }

    private static <E> List<E> collectComponents(Node<E> head, Deque<Node<E>> deque) {
        var ret = new LinkedList<E>();
        var node = (Node<E>) null;
        do {
            node = deque.pop();
            node.onStack = false;
            ret.add(node.value);
        } while (node != head);
        return ret;
    }

    private static <E> int processNode(Node<E> node, Deque<Node<E>> deque, List<List<E>> components, int time) {
        var stack = new LinkedList<Entry<E>>();
        stack.push(new Entry<>(node, 0));
        while (!stack.isEmpty()) {
            var entry = stack.pop();
            var current = entry.node;
            var local = entry.time;
            var adjacents = current.adjacents;
            if (local != 0) {
                current.low = Math.min(current.low, adjacents.get(local - 1).low);
            } else {
                current.index = time;
                current.low = time;
                ++time;
                deque.push(current);
                current.onStack = true;
            }
            var size = adjacents.size();
            while (local < size && adjacents.get(local).index != -1) {
                var w = adjacents.get(local);
                if (w.onStack) {
                    current.low = Math.min(current.low, w.index);
                }
                ++local;
            }
            if (local < size) {
                var w = adjacents.get(local);
                stack.push(new Entry<>(current, local + 1));
                stack.push(new Entry<>(w, 0));
                continue;
            }
            if (current.low != current.index) {
                continue;
            }
            components.add(collectComponents(current, deque));
        }
        return time;
    }

    public static <E> List<List<E>> findStronglyConnectedComponents(Graph<E> graph) {
        var ret = new LinkedList<List<E>>();
        var nodes = graphToNodes(graph);
        var time = 0;
        var deque = new LinkedList<Node<E>>();
        for (var node : nodes) {
            if (node.index != -1) {
                continue;
            }
            time = processNode(node, deque, ret, time);
        }
        return ret;
    }

    private static final class Node<E> {
        E value;
        int index;
        int low;
        List<Node<E>> adjacents;
        boolean onStack;

        Node(E value) {
            this.value = value;
            this.index = -1;
            this.low = -1;
        }
    }

    private static final class Entry<E> {
        Node<E> node;
        int time;

        Entry(Node<E> node, int time) {
            this.node = node;
            this.time = time;
        }
    }
}
