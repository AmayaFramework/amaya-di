package com.github.romanqed.jgraph;

import java.util.List;

final class Node<E> {
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
