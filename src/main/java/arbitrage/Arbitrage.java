package arbitrage;

import static java.lang.String.format;
import static java.util.Comparator.naturalOrder;
import static sequences.Sequences.cons;
import static sequences.Sequences.empty;
import static sequences.Sequences.intersection;
import static sequences.Sequences.sequence;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import sequences.Sequence;
import sequences.Sequences;

public class Arbitrage {

    public static void main(String[] args) throws IOException {
        try (InputStream testInput = Arbitrage.class.getClassLoader()
                .getResourceAsStream(
                        "arbitrage/test-input.txt")) {
            System.setIn(testInput);
            _main();
        }
    }

    public static void _main() {
        Graph g = new Graph(sequence(
                new Edge(0, 1, new BigDecimal("3.1")),
                new Edge(0, 2, new BigDecimal("0.0023")),
                new Edge(0, 3, new BigDecimal("0.35")),
                new Edge(1, 0, new BigDecimal("0.21")),
                new Edge(1, 2, new BigDecimal("0.00353")),
                new Edge(1, 3, new BigDecimal("8.13")),
                new Edge(2, 0, new BigDecimal("200")),
                new Edge(2, 1, new BigDecimal("180.559")),
                new Edge(2, 3, new BigDecimal("10.339")),
                new Edge(3, 0, new BigDecimal("2.11")),
                new Edge(3, 1, new BigDecimal("0.089")),
                new Edge(3, 2, new BigDecimal("0.06111"))));
        g.withBiggestFactors().print();
    }
}

class Graph {
    
    private Sequence<Edge> edges = empty();

    public Graph(Sequence<Edge> edges) {
        this.edges = edges;
    }

    public void addEdge(int from, int to, BigDecimal factor) {
        edges = cons(new Edge(from, to, factor, to), edges);
    }
    
    public Graph withBiggestFactors() {
        Sequence<Integer> vertices = getVertices();
        return vertices.reduce(this, (g0, k) ->
                vertices.reduce(g0, (g1, i) ->
                        vertices.reduce(g1, (g2, j) ->
                                g2.update(i, j, k))));
    }

    private Graph update(Integer i, Integer j, Integer k) {
        if (i == j | j == k | k == i) return this;
        Edge ij = getEdge(i, j).first();
        Edge ik = getEdge(i, k).first();
        Edge kj = getEdge(k, j).first();
        // duplicates other than k
        if (intersection(path(ik), path(kj)).size() > 1) return this;
        BigDecimal alternativeFactor = ik.factor().multiply(kj.factor());
        if (alternativeFactor.compareTo(ij.factor()) > 0) {
            return withReplacedEdge(ij, alternativeFactor, ik.waypoint());
        }
        return this;
    }

    private Graph withReplacedEdge(Edge ij,
            BigDecimal factor, int waypoint) {
        return new Graph(edges.map(edge -> edge == ij
                ? new Edge(ij.from(), ij.to(), factor, waypoint)
                : edge));
    }

    private Sequence<Integer> path(Edge edge) {
        return cons(edge.from(), edge.waypoint() == edge.to()
                ? sequence(edge.waypoint())
                : path(getEdge(edge.waypoint(), edge.to()).first()));
    }

    private Sequence<Integer> getVertices() {
        return edges
                .reduce(Sequences.<Integer>empty(), (vertices, edge) ->
                        cons(edge.from(), cons(edge.to(), vertices)))
                .distinct()
                .sort(naturalOrder());
    }

    public void print() {
        Sequence<Integer> vertices = getVertices();
        vertices.forEach(from -> {
            vertices.forEach(to -> {
                Sequence<Edge> e = getEdge(from, to);
                System.out.print(e.isEmpty()
                        ? format("%12s", "")
                        : format("%6d:%5.2f", e.first().waypoint(), e.first().factor()));
            });
            System.out.print("\n");
        });
        edges.forEach(edge -> {
            System.out.printf("%d %d %s\n", edge.from(), edge.to(), path(edge));
        });
        System.out.print("---------------\n");
    }

    private Sequence<Edge> getEdge(int from, int to) {
        return edges.filter(e -> e.from() == from && e.to() == to);
    }
}

class Edge {
    
    private int from;
    private int to;
    private BigDecimal factor = BigDecimal.ZERO;
    private int waypoint;
    
    public Edge(int from, int to, BigDecimal factor, int waypoint) {
        this.from = from;
        this.to = to;
        this.factor = factor;
        this.waypoint = waypoint;
    }

    public Edge(int from, int to, BigDecimal factor) {
        this(from, to, factor, to);
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public BigDecimal factor() {
        return factor;
    }

    public int waypoint() {
        return waypoint;
    }
}
