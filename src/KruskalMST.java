import edu.princeton.cs.algorithms.*;
import edu.princeton.cs.introcs.*;

import java.util.Arrays;
import java.util.Comparator;

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

        // check optimality conditions
        assert check(G);
    }

    public Metrics getMetrics() { return metrics; } // accessor

    public Iterable<Edge> edges() {
        return mst;
    }

    public double weight() {
        return weight;
    }

    private boolean check(EdgeWeightedGraph G) {
        double total = 0.0;
        for (Edge e : edges()) {
            total += e.weight();
        }
        if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
            return false;
        }

        UF uf = new UF(G.V());
        for (Edge e : edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) == uf.find(w)) {
                System.err.println("Not a forest");
                return false;
            }
            uf.union(v, w);
        }

        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) != uf.find(w)) {
                System.err.println("Not a spanning forest");
                return false;
            }
        }

        for (Edge e : edges()) {
            uf = new UF(G.V());
            for (Edge f : mst) {
                int x = f.either(), y = f.other(x);
                if (f != e) uf.union(x, y);
            }
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (uf.find(x) != uf.find(y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f + " violates cut optimality conditions");
                        return false;
                    }
                }
            }

        }

        return true;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        KruskalMST mst = new KruskalMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n", mst.weight());

        // print metrics
        System.out.println("--- Metrics ---");
        System.out.println(mst.getMetrics().toString());
        System.out.println(mst.getMetrics().toJsonWithGraphInfo(G.V(), G.E(), mst.weight()));
    }

}