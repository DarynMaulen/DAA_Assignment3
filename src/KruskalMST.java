import edu.princeton.cs.algorithms.*;

import java.util.Arrays;
import java.util.Comparator;

// Implementation of Kruskal's algorithm for finding Minimum Spanning Tree.
// Uses Union-Find data structure to efficiently detect cycles and build MST
// by processing edges in sorted order by weight.
public class KruskalMST {
    private static final double FLOATING_POINT_EPSILON = 1.0E-12;

    private double weight;                        // weight of MST
    private Queue<Edge> mst = new Queue<Edge>();  // edges in MST

    private Metrics metrics;

    public KruskalMST(EdgeWeightedGraph G) {
        this.metrics = new Metrics("Kruskal");
        metrics.startTimer();

        // create array of edges
        Edge[] edges = new Edge[G.E()];
        int t = 0;
        for (Edge e: G.edges()) {
            edges[t++] = e;
        }

        // sort with comparator that increments comparison counter
        Arrays.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge a, Edge b) {
                metrics.incComparisons();
                return Double.compare(a.weight(), b.weight());
            }
        });

        // instrumented UF
        InstrumentedUF iuf = new InstrumentedUF(G.V(), metrics);

        // run greedy algorithm
        for (int i = 0; i < G.E() && mst.size() < G.V() - 1; i++) {
            Edge e = edges[i];
            metrics.incEdgeInspected();
            int v = e.either();
            int w = e.other(v);

            // v-w does not create a cycle
            if (iuf.find(v) != iuf.find(w)) {
                iuf.union(v, w);     // merge v and w components
                mst.enqueue(e);     // add edge e to mst
                weight += e.weight();
            }
        }

        metrics.stopTimer();
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Iterable<Edge> edges() {
        return mst;
    }

    public double weight() {
        return weight;
    }
}