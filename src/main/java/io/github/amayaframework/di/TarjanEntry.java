package io.github.amayaframework.di;

final class TarjanEntry {
    GraphNode node;
    int time;

    TarjanEntry(GraphNode node, int time) {
        this.node = node;
        this.time = time;
    }
}
