import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Counts strongly connected components using Tarjan's algorithm.
 * The DFS is implemented iteratively so a long dependency chain does not
 * overflow the Java call stack.
 */
public class Problem06 {
    public int countDependencyGroups(int n, int[][] dependencies) {
        if (n <= 0) {
            return 0;
        }

        List<Integer>[] graph = createGraph(n);
        if (dependencies != null) {
            for (int[] edge : dependencies) {
                graph[edge[0]].add(edge[1]);
            }
        }

        int[] discovery = new int[n];
        int[] low = new int[n];
        boolean[] onStack = new boolean[n];
        int nextIndex = 1;
        int componentCount = 0;

        Deque<Integer> componentStack = new ArrayDeque<>();
        Deque<DfsFrame> dfs = new ArrayDeque<>();

        for (int start = 0; start < n; start++) {
            if (discovery[start] != 0) {
                continue;
            }

            discovery[start] = low[start] = nextIndex++;
            componentStack.push(start);
            onStack[start] = true;
            dfs.push(new DfsFrame(start, -1));

            while (!dfs.isEmpty()) {
                DfsFrame frame = dfs.peek();
                int vertex = frame.vertex;

                if (frame.nextNeighbor < graph[vertex].size()) {
                    int neighbor = graph[vertex].get(frame.nextNeighbor++);

                    if (discovery[neighbor] == 0) {
                        discovery[neighbor] = low[neighbor] = nextIndex++;
                        componentStack.push(neighbor);
                        onStack[neighbor] = true;
                        dfs.push(new DfsFrame(neighbor, vertex));
                    } else if (onStack[neighbor]) {
                        low[vertex] = Math.min(low[vertex], discovery[neighbor]);
                    }
                } else {
                    dfs.pop();

                    if (frame.parent != -1) {
                        low[frame.parent] = Math.min(low[frame.parent], low[vertex]);
                    }

                    if (low[vertex] == discovery[vertex]) {
                        int member;
                        do {
                            member = componentStack.pop();
                            onStack[member] = false;
                        } while (member != vertex);
                        componentCount++;
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
        final int parent;
        int nextNeighbor;

        DfsFrame(int vertex, int parent) {
            this.vertex = vertex;
            this.parent = parent;
        }
    }
}
