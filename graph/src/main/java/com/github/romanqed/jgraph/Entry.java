package com.github.romanqed.jgraph;

final class Entry<E> {
    Node<E> node;
    int time;

    Entry(Node<E> node, int time) {
        this.node = node;
        this.time = time;
    }
}
