import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Counts strongly connected components using Kosaraju's two-pass algorithm.
 * Both DFS passes are iterative to handle the stated limit of 100,000 vertices
 * without risking Java call-stack overflow.
 */
public class Problem05 {
    public int countDependencyGroups(int n, int[][] dependencies) {
        if (n <= 0) {
            return 0;
        }

        List<Integer>[] graph = createGraph(n);
        List<Integer>[] reverseGraph = createGraph(n);

        if (dependencies != null) {
            for (int[] edge : dependencies) {
                int from = edge[0];
                int to = edge[1];
                graph[from].add(to);
                reverseGraph[to].add(from);
            }
        }

        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);
        Deque<DfsFrame> dfs = new ArrayDeque<>();

        // First pass: vertices ordered by DFS finishing time.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            visited[start] = true;
            dfs.push(new DfsFrame(start));

            while (!dfs.isEmpty()) {
                DfsFrame frame = dfs.peek();
                if (frame.nextNeighbor < graph[frame.vertex].size()) {
                    int neighbor = graph[frame.vertex].get(frame.nextNeighbor++);
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        dfs.push(new DfsFrame(neighbor));
                    }
                } else {
                    finishOrder.add(frame.vertex);
                    dfs.pop();
                }
            }
        }

        // Second pass: each traversal in the transposed graph is one SCC.
        boolean[] assigned = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        int componentCount = 0;

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int start = finishOrder.get(i);
            if (assigned[start]) {
                continue;
            }

            componentCount++;
            assigned[start] = true;
            stack.push(start);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                for (int neighbor : reverseGraph[vertex]) {
                    if (!assigned[neighbor]) {
                        assigned[neighbor] = true;
                        stack.push(neighbor);
                    }
                }
            }
        }

        return componentCount;
    }

    @SuppressWarnings("unchecked")
    private List<Integer>[] createGraph(int n) {
        List<Integer>[] graph = (List<Integer>[]) new List<?>[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        return graph;
    }

    private static final class DfsFrame {
        final int vertex;
        int nextNeighbor;

        DfsFrame(int vertex) {
            this.vertex = vertex;
        }
    }
}
