import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;

import java.io.File;
import java.io.FileWriter;
import java.util.*;


// Main runner class that orchestrates MST algorithm execution and result collection.
// Reads input graphs, runs both Prim and Kruskal algorithms, compares results,
// and generates comprehensive output JSON with performance metrics.
public class MSTRunner {

    private static final String INPUT_FILE_PATH = "data" + File.separator + "graphs.json";
    private static final String OUTPUT_FILE_PATH = "results" + File.separator + "output.json";

    // Helper data classes for JSON aggregation
    static class EdgeOut {
        String from;
        String to;
        double weight;
        EdgeOut(String a, String b, double w) { from = a; to = b; weight = w; }
    }

    static class AlgoOut {
        List<EdgeOut> mst_edges;
        double total_cost;
        long comparisons;
        long finds;
        long unions;
        long pqInserts;
        long pqDelMins;
        long pqDecreaseKeys;
        long edgesInspected;
        double execution_time_ms;
    }

    static class GraphResult {
        int graph_id;
        Map<String, Integer> input_stats;
        AlgoOut prim;
        AlgoOut kruskal;
    }

    static class FinalOut {
        List<GraphResult> results = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        File inFile = new File(INPUT_FILE_PATH);
        if (!inFile.exists()) {
            System.err.println("FATAL: Missing input file: " + INPUT_FILE_PATH);
            System.err.println("Please create the file 'data/graphs.json'.");
            return;
        }

        System.out.println("Reading input from: " + INPUT_FILE_PATH);
        List<JsonMultiReader.GraphSpec> specs = JsonMultiReader.read(INPUT_FILE_PATH);

        FinalOut finalOut = new FinalOut();

        for (JsonMultiReader.GraphSpec spec : specs) {
            EdgeWeightedGraph G = spec.graph;
            String[] labels = spec.indexToLabel;

            System.out.printf("  -> Processing Graph ID %d (V=%d, E=%d)...%n", spec.id, G.V(), G.E());

            // Run Kruskal and Prim
            KruskalMST k = new KruskalMST(G);
            PrimMST p = new PrimMST(G);

            // Collect metrics
            AlgoOut kOut = buildAlgoOut(k.edges(), k.weight(), k.getMetrics(), labels);
            AlgoOut pOut = buildAlgoOut(p.edges(), p.weight(), p.getMetrics(), labels);

            boolean kValid = MSTValidator.validate(G, k.edges(), k.weight());
            boolean pValid = MSTValidator.validate(G, p.edges(), p.weight());

            System.out.printf("  [VALIDATION] Kruskal valid: %b | Prim valid: %b%n", kValid, pValid);

            // Compare MST weights
            if (Math.abs(k.weight() - p.weight()) > 1e-9) {
                System.err.printf("  WARNING: MST weights differ for Graph %d: Kruskal=%.6f, Prim=%.6f%n",
                        spec.id, k.weight(), p.weight());
            }

            // Build graph result
            GraphResult gr = new GraphResult();
            gr.graph_id = spec.id;

            Map<String, Integer> stats = new LinkedHashMap<>();
            stats.put("vertices", G.V());
            stats.put("edges", G.E());
            gr.input_stats = stats;

            gr.prim = pOut;
            gr.kruskal = kOut;
            finalOut.results.add(gr);
        }

        // Write final aggregated results to JSON
        new File("results").mkdirs();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(OUTPUT_FILE_PATH)) {
            gson.toJson(finalOut, fw);
        }
        System.out.println("\nAll results aggregated and written to: " + OUTPUT_FILE_PATH);
    }

    // Builds an AlgoOut object from MST data
    private static AlgoOut buildAlgoOut(Iterable<Edge> edges, double totalWeight, Metrics m, String[] labels) {
        AlgoOut ao = new AlgoOut();
        ao.mst_edges = new ArrayList<>();

        // Collect MST edges
        for (Edge e : edges) {
            int v = e.either();
            int w = e.other(v);
            ao.mst_edges.add(new EdgeOut(labels[v], labels[w], e.weight()));
        }
        ao.total_cost = totalWeight;

        // Collect metrics
        ao.comparisons = m.getComparisons();
        ao.finds = m.getFinds();
        ao.unions = m.getUnions();
        ao.pqInserts = m.getPqInserts();
        ao.pqDelMins = m.getPqDelMins();
        ao.pqDecreaseKeys = m.getPqDecreaseKeys();
        ao.edgesInspected = m.getEdgesInspected();
        ao.execution_time_ms = m.getElapsedMs();

        return ao;
    }
}