/**
 * Minimum spanning tree using Prim's algorithm with an array-based priority
 * selection. This O(V^2) implementation is efficient for the dense complete
 * graph formed by the point set and uses O(V) auxiliary space.
 */
public class Problem04 {
    public int minCostConnectPoints(int[][] points) {
        if (points == null || points.length <= 1) {
            return 0;
        }

        int n = points.length;
        long[] minDistance = new long[n];
        boolean[] inMst = new boolean[n];

        for (int i = 0; i < n; i++) {
            minDistance[i] = Long.MAX_VALUE;
        }
        minDistance[0] = 0L;

        long totalCost = 0L;

        for (int step = 0; step < n; step++) {
            int next = -1;
            for (int vertex = 0; vertex < n; vertex++) {
                if (!inMst[vertex]
                        && (next == -1 || minDistance[vertex] < minDistance[next])) {
                    next = vertex;
                }
            }

            inMst[next] = true;
            totalCost += minDistance[next];

            for (int vertex = 0; vertex < n; vertex++) {
                if (!inMst[vertex]) {
                    long distance = Math.abs((long) points[next][0] - points[vertex][0])
                                  + Math.abs((long) points[next][1] - points[vertex][1]);
                    if (distance < minDistance[vertex]) {
                        minDistance[vertex] = distance;
                    }
                }
            }
        }

        return Math.toIntExact(totalCost);
    }
}
