import com.google.gson.*;
import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

// JSON reader for graph data files. Parses graph specifications from JSON format
// and converts them to EdgeWeightedGraph objects with label mapping.
public class JsonMultiReader {

    public static class GraphSpec {
        public int id;
        public String description;
        public EdgeWeightedGraph graph;
        public String[] indexToLabel;
    }

    public static List<GraphSpec> read(String path) throws Exception {
        Gson gson = new Gson();
        List<GraphSpec> list = new ArrayList<>();

        try (Reader r = new FileReader(path)) {
            JsonObject root = gson.fromJson(r, JsonObject.class);
            JsonArray graphs = root.getAsJsonArray("graphs");

            for (JsonElement ge : graphs) {
                JsonObject g = ge.getAsJsonObject();

                int id = g.has("id") ? g.get("id").getAsInt() : 0;
                String desc = g.has("description") ? g.get("description").getAsString() : "";

                // Read vertex labels
                JsonArray nodesArr = g.getAsJsonArray("nodes");
                List<String> nodes = new ArrayList<>();
                for (JsonElement ne : nodesArr) nodes.add(ne.getAsString());

                // Create label-to-index map
                Map<String, Integer> labelToIndexMap = new HashMap<>();
                for (int i = 0; i < nodes.size(); i++) {
                    labelToIndexMap.put(nodes.get(i), i);
                }

                // Build EdgeWeightedGraph
                EdgeWeightedGraph G = new EdgeWeightedGraph(nodes.size());
                JsonArray edgesArr = g.getAsJsonArray("edges");
                for (JsonElement ee : edgesArr) {
                    JsonObject e = ee.getAsJsonObject();
                    String from = e.get("from").getAsString();
                    String to = e.get("to").getAsString();
                    double w = e.get("weight").getAsDouble();

                    G.addEdge(new Edge(labelToIndexMap.get(from), labelToIndexMap.get(to), w));
                }

                // Assemble graph specification
                GraphSpec spec = new GraphSpec();
                spec.id = id;
                spec.description = desc;
                spec.graph = G;
                spec.indexToLabel = nodes.toArray(new String[0]);
                list.add(spec);
            }

            return list;
        }
    }
}