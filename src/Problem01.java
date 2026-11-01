import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Counts connected components in an undirected graph represented by an
 * adjacency matrix. Uses iterative depth-first search to avoid call-stack
 * overflow on large inputs.
 */
public class Problem01 {
    public int findCircleNum(int[][] isConnected) {
        if (isConnected == null || isConnected.length == 0) {
            return 0;
        }

        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;
        Deque<Integer> stack = new ArrayDeque<>();

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            provinces++;
            visited[start] = true;
            stack.push(start);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (isConnected[vertex][neighbor] == 1 && !visited[neighbor]) {
                        visited[neighbor] = true;
                        stack.push(neighbor);
                    }
                }
            }
        }

        return provinces;
    }
}
