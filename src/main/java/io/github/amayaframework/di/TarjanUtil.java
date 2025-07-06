package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.*;

final class TarjanUtil {
    private TarjanUtil() {
    }

    static List<Type> collectComponents(GraphNode head, Deque<GraphNode> deque) {
        var size = deque.size();
        if (size == 0) {
            return null;
        }
        GraphNode node;
        if (size == 1) {
            node = deque.pop();
            node.onStack = false;
            return null;
        }
        if (deque.peek() == head) {
            node = deque.pop();
            node.onStack = false;
            return null;
        }
        var ret = new LinkedList<Type>();
        do {
            node = deque.pop();
            node.onStack = false;
            ret.add(node.value);
        } while (node != head);
        return ret;
    }

    static int processNode(GraphNode node, Deque<GraphNode> deque, List<List<Type>> components, int time) {
        var stack = new LinkedList<TarjanEntry>();
        stack.push(new TarjanEntry(node, 0));
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
            if (adjacents == null) {
                var collected = collectComponents(current, deque);
                if (collected != null) {
                    components.add(collected);
                }
                continue;
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
                stack.push(new TarjanEntry(current, local + 1));
                stack.push(new TarjanEntry(w, 0));
                continue;
            }
            if (current.low != current.index) {
                continue;
            }
            var collected = collectComponents(current, deque);
            if (collected != null) {
                components.add(collected);
            }
        }
        return time;
    }

    static List<List<Type>> findSCC(Collection<GraphNode> nodes) {
        var ret = new LinkedList<List<Type>>();
        var deque = new LinkedList<GraphNode>();
        var time = 0;
        for (var node : nodes) {
            if (node.index != -1) {
                continue;
            }
            time = processNode(node, deque, ret, time);
        }
        return ret;
    }
}
