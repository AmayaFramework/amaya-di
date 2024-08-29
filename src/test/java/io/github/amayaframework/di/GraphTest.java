package io.github.amayaframework.di;

import io.github.amayaframework.di.graph.Graph;
import io.github.amayaframework.di.graph.GraphUtil;
import io.github.amayaframework.di.graph.HashGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class GraphTest extends Assertions {
    public void testGraph(Graph<Integer> graph) {
        /*
        0 -> 1 -> 2 -> 4
             |    |
             3    5
         */
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 4);
        graph.addEdge(1, 3);
        graph.addEdge(2, 5);
        assertAll(
                () -> assertEquals(6, graph.size()),
                () -> assertTrue(graph.contains(0)),
                () -> assertTrue(graph.contains(1)),
                () -> assertTrue(graph.contains(2)),
                () -> assertTrue(graph.contains(3)),
                () -> assertTrue(graph.contains(4)),
                () -> assertTrue(graph.contains(5)),
                () -> assertEquals(Set.of(1), graph.getAdjacentNodes(0)),
                () -> assertEquals(Set.of(2, 3), graph.getAdjacentNodes(1)),
                () -> assertEquals(Set.of(4, 5), graph.getAdjacentNodes(2))
        );
    }

    @Test
    public void testHashGraph() {
        testGraph(new HashGraph<>());
    }

    public void testSSC(Graph<Integer> graph, Set<Set<Integer>> cycles) {
        var found = GraphUtil.findSCC(graph);
        assertAll(
                () -> assertEquals(cycles.size(), found.size()),
                () -> {
                    var actual = new HashSet<Set<Integer>>();
                    found.forEach(e -> actual.add(new HashSet<>(e)));
                    assertEquals(cycles, actual);
                }
        );
    }

    public void testSSC1(Graph<Integer> graph) {
        graph.addEdge(1, 0);
        graph.addEdge(0, 2);
        graph.addEdge(2, 1);
        graph.addEdge(0, 3);
        graph.addEdge(3, 4);
        testSSC(graph, Set.of(
                Set.of(4),
                Set.of(3),
                Set.of(1, 2, 0)
        ));
    }

    @Test
    public void testHashSSC1() {
        testSSC1(new HashGraph<>());
    }

    public void testSSC2(Graph<Integer> graph) {
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        testSSC(graph, Set.of(
                Set.of(3),
                Set.of(2),
                Set.of(1),
                Set.of(0)
        ));
    }

    @Test
    public void testHashSSC2() {
        testSSC2(new HashGraph<>());
    }

    public void testSSC3(Graph<Integer> graph) {
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(1, 6);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);
        testSSC(graph, Set.of(
                Set.of(5),
                Set.of(3),
                Set.of(4),
                Set.of(6),
                Set.of(2, 1, 0)
        ));
    }

    @Test
    public void testHashSSC3() {
        testSSC3(new HashGraph<>());
    }

    public void testSSC4(Graph<Integer> graph) {
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 4);
        graph.addEdge(2, 0);
        graph.addEdge(2, 6);
        graph.addEdge(3, 2);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(5, 8);
        graph.addEdge(5, 9);
        graph.addEdge(6, 4);
        graph.addEdge(7, 9);
        graph.addEdge(8, 9);
        graph.addEdge(9, 8);
        testSSC(graph, Set.of(
                Set.of(8, 9),
                Set.of(7),
                Set.of(5, 4, 6),
                Set.of(3, 2, 1, 0)
        ));
    }

    @Test
    public void testHashSSC4() {
        testSSC4(new HashGraph<>());
    }

    public void testSSC5(Graph<Integer> graph) {
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 0);
        graph.addEdge(4, 2);
        testSSC(graph, Set.of(
                Set.of(4, 3, 2, 1, 0)
        ));
    }

    @Test
    public void testHashSSC5() {
        testSSC5(new HashGraph<>());
    }
}
