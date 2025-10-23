import edu.princeton.cs.algorithms.UF;

/**
 * Wrapper around Princeton's UF to count find/union operations via Metrics.
 */
public class InstrumentedUF {
    private final UF uf;
    private final Metrics metrics;

    public InstrumentedUF(int n, Metrics metrics) {
        this.uf = new UF(n);
        this.metrics = metrics;
    }

    public int find(int x) {
        metrics.incFinds();
        return uf.find(x);
    }

    public void union(int x, int y) {
        metrics.incUnions();
        uf.union(x, y);
    }
}
