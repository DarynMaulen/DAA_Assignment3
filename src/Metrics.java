import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Metrics collection utility for tracking algorithm performance and operations.
// Counts comparisons, union-find operations, PQ operations, and execution time.
// Provides JSON serialization for result reporting.
public class Metrics {
    private final String algorithm;
    private long comparisons = 0;
    private long finds = 0;
    private long unions = 0;
    private long edgesInspected = 0;

    // priority-queue ops
    private long pqInserts = 0;
    private long pqDelMins = 0;
    private long pqDecreaseKeys = 0;

    private long startNano = 0;
    private double elapsedMs = 0.0;

    public Metrics(String algorithm) {
        this.algorithm = algorithm;
    }

    // timing
    public void startTimer() {
        startNano = System.nanoTime();
    }

    public void stopTimer() {
        if (startNano != 0) {
            elapsedMs = (System.nanoTime() - startNano) / 1_000_000.0;
            startNano = 0;
        }
    }

    // incrementers
    public void incComparisons() {
        comparisons++;
    }

    public void incFinds() {
        finds++;
    }

    public void incUnions() {
        unions++;
    }

    public void incEdgeInspected() {
        edgesInspected++;
    }

    // PQ counters
    public void incPqInserts() {
        pqInserts++;
    }

    public void incPqDelMins() {
        pqDelMins++;
    }

    public void incPqDecreaseKeys() {
        pqDecreaseKeys++;
    }

    // getters
    public long getComparisons() {
        return comparisons;
    }

    public long getFinds() {
        return finds;
    }

    public long getUnions() {
        return unions;
    }

    public long getEdgesInspected() {
        return edgesInspected;
    }

    public long getPqInserts() {
        return pqInserts;
    }

    public long getPqDelMins() {
        return pqDelMins;
    }

    public long getPqDecreaseKeys() {
        return pqDecreaseKeys;
    }

    public double getElapsedMs() {
        return elapsedMs;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        return String.format("%s: time=%d ms, comps=%d, finds=%d, unions=%d, edgesInspected=%d, pqInserts=%d, pqDelMins=%d, pqDecreaseKeys=%d",
                algorithm, elapsedMs, comparisons, finds, unions, edgesInspected, pqInserts, pqDelMins, pqDecreaseKeys);
    }

    // JSON with extra graph info
    public String toJsonWithGraphInfo(int V, int E, double mstWeight) {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        JsonOut out = new JsonOut();
        out.algorithm = algorithm;
        out.timeMs = elapsedMs;
        out.comparisons = comparisons;
        out.finds = finds;
        out.unions = unions;
        out.edgesInspected = edgesInspected;
        out.pqInserts = pqInserts;
        out.pqDelMins = pqDelMins;
        out.pqDecreaseKeys = pqDecreaseKeys;
        out.V = V;
        out.E = E;
        out.mstWeight = mstWeight;
        return g.toJson(out);
    }

    // helper structure for JSON serialization
    private static class JsonOut {
        String algorithm;
        double timeMs;
        long comparisons;
        long finds;
        long unions;
        long edgesInspected;
        long pqInserts;
        long pqDelMins;
        long pqDecreaseKeys;
        int V;
        int E;
        double mstWeight;
    }
}