import edu.princeton.cs.algorithms.*;

// Implementation of Prim's algorithm for finding Minimum Spanning Tree.
// Uses priority queue to efficiently select the next minimum weight edge
// and grows the MST from an arbitrary starting vertex.
public class PrimMST {
    private static final double FLOATING_POINT_EPSILON = 1.0E-12;

    private Edge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private InstrumentedIndexMinPQ<Double> pq;
    private Metrics metrics;

    public PrimMST(EdgeWeightedGraph G) {
        this.metrics = new Metrics("Prim");

        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;

        metrics.startTimer();
        pq = new InstrumentedIndexMinPQ<>(G.V(), metrics);
        for (int v = 0; v < G.V(); v++)      // run from each vertex to find
            if (!marked[v]) prim(G, v);      // minimum spanning forest
        metrics.stopTimer();
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(EdgeWeightedGraph G, int s) {
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            scan(G, v);
        }
    }

    // scan vertex v
    private void scan(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            metrics.incEdgeInspected();
            if (marked[w]) continue;
            metrics.incComparisons();
            if (e.weight() < distTo[w]) {
                distTo[w] = e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)){
                    pq.decreaseKey(w, distTo[w]);
                }
                else{
                    pq.insert(w, distTo[w]);
                }
            }
        }
    }

    public Metrics getMetrics() { return metrics; } // accessor

    public Iterable<Edge> edges() {
        Queue<Edge> mst = new Queue<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.enqueue(e);
            }
        }
        return mst;
    }

    public double weight() {
        double weight = 0.0;
        for (Edge e : edges())
            weight += e.weight();
        return weight;
    }
}
