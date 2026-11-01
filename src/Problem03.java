import java.util.Arrays;

/**
 * Minimum spanning tree using Kruskal's algorithm.
 * The complete graph has one edge for every pair of points.
 */
public class Problem03 {
    public int minCostConnectPoints(int[][] points) {
        if (points == null || points.length <= 1) {
            return 0;
        }

        int n = points.length;
        Edge[] edges = new Edge[n * (n - 1) / 2];
        int edgeIndex = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long weight = Math.abs((long) points[i][0] - points[j][0])
                            + Math.abs((long) points[i][1] - points[j][1]);
                edges[edgeIndex++] = new Edge(i, j, weight);
            }
        }

        Arrays.sort(edges, (a, b) -> Long.compare(a.weight, b.weight));

        DisjointSet dsu = new DisjointSet(n);
        long totalCost = 0L;
        int selectedEdges = 0;

        for (Edge edge : edges) {
            if (dsu.union(edge.u, edge.v)) {
                totalCost += edge.weight;
                selectedEdges++;
                if (selectedEdges == n - 1) {
                    break;
                }
            }
        }

        return Math.toIntExact(totalCost);
    }

    private static final class Edge {
        final int u;
        final int v;
        final long weight;

        Edge(int u, int v, long weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private static final class DisjointSet {
        private final int[] parent;
        private final int[] size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        boolean union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA == rootB) {
                return false;
            }
            if (size[rootA] < size[rootB]) {
                int temp = rootA;
                rootA = rootB;
                rootB = temp;
            }
            parent[rootB] = rootA;
            size[rootA] += size[rootB];
            return true;
        }
    }
}
