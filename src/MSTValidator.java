import edu.princeton.cs.algorithms.*;

// Utility to validate a candidate MST for an EdgeWeightedGraph.
public class MSTValidator {

    private static final double EPS = 1.0E-12;

    public static boolean validate(EdgeWeightedGraph G, Iterable<Edge> mstEdges, double declaredWeight) {
        // weight check
        double total = 0.0;
        for (Edge e : mstEdges) total += e.weight();
        if (Math.abs(total - declaredWeight) > EPS) {
            System.err.printf("Weight mismatch: sum=%f declared=%f\n", total, declaredWeight);
            return false;
        }

        // acyclic
        UF uf = new UF(G.V());
        for (Edge e : mstEdges) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) == uf.find(w)) {
                System.err.println("Not a forest: cycle found with edge " + e);
                return false;
            }
            uf.union(v, w);
        }

        // spanning forest: every original edge's endpoints connected in the MST UF
        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) != uf.find(w)) {
                System.err.println("Not a spanning forest: vertices " + v + " and " + w + " are disconnected in MST");
                return false;
            }
        }

        // minimality
        for (Edge e : mstEdges) {
            // build UF of MST without edge e
            UF cutUF = new UF(G.V());
            for (Edge f : mstEdges) {
                if (f == e) continue;
                int x = f.either(), y = f.other(x);
                cutUF.union(x, y);
            }
            // check that no crossing edge has smaller weight
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (cutUF.find(x) != cutUF.find(y)) {
                    if (f.weight() < e.weight() - EPS) {
                        System.err.println("Cut optimality violated: " + f + " is lighter than MST edge " + e);
                        return false;
                    }
                }
            }
        }

        // all checks passed
        return true;
    }
}