import edu.princeton.cs.algorithms.IndexMinPQ;

// Instrumented Index Minimum Priority Queue that tracks queue operations.
// Wraps Princeton's IndexMinPQ to collect metrics for performance analysis.
public class InstrumentedIndexMinPQ<Key extends Comparable<Key>> {
    private final IndexMinPQ<Key> pq;
    private final Metrics metrics;

    public InstrumentedIndexMinPQ(int maxN, Metrics metrics) {
        this.pq = new IndexMinPQ<>(maxN);
        this.metrics = metrics;
    }

    public void insert(int k, Key key) {
        metrics.incPqInserts();
        pq.insert(k, key);
    }

    public int delMin() {
        metrics.incPqDelMins();
        return pq.delMin();
    }

    public void decreaseKey(int k, Key key) {
        metrics.incPqDecreaseKeys();
        pq.decreaseKey(k, key);
    }

    public boolean contains(int k) {
        return pq.contains(k);
    }

    public boolean isEmpty() {
        return pq.isEmpty();
    }

    public int size() {
        return pq.size();
    }
}
