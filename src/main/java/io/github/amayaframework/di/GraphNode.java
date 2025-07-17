package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

final class GraphNode {
    Type value;
    int index;
    int low;
    List<GraphNode> adjacents;
    boolean onStack;

    GraphNode(Type value) {
        this.value = value;
        this.index = -1;
        this.low = -1;
    }
}
