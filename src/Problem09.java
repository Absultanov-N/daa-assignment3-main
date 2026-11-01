import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Returns a valid course order using DFS reverse finishing order.
 * Returns an empty array if the prerequisite graph contains a directed cycle.
 */
public class Problem09 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        if (numCourses == 0) {
            return new int[0];
        }

        List<Integer>[] graph = buildGraph(numCourses, prerequisites);
        byte[] state = new byte[numCourses];
        int[] order = new int[numCourses];
        int orderSize = 0;
        Deque<DfsFrame> stack = new ArrayDeque<>();

        for (int start = 0; start < numCourses; start++) {
            if (state[start] != 0) {
                continue;
            }

            state[start] = 1;
            stack.push(new DfsFrame(start));

            while (!stack.isEmpty()) {
                DfsFrame frame = stack.peek();

                if (frame.nextNeighbor < graph[frame.vertex].size()) {
                    int neighbor = graph[frame.vertex].get(frame.nextNeighbor++);
                    if (state[neighbor] == 1) {
                        return new int[0];
                    }
                    if (state[neighbor] == 0) {
                        state[neighbor] = 1;
                        stack.push(new DfsFrame(neighbor));
                    }
                } else {
                    state[frame.vertex] = 2;
                    order[orderSize++] = frame.vertex;
                    stack.pop();
                }
            }
        }

        // DFS finishing order is reversed to obtain prerequisite-first order.
        for (int left = 0, right = orderSize - 1; left < right; left++, right--) {
            int temp = order[left];
            order[left] = order[right];
            order[right] = temp;
        }

        return order;
    }

    @SuppressWarnings("unchecked")
    private List<Integer>[] buildGraph(int n, int[][] prerequisites) {
        List<Integer>[] graph = (List<Integer>[]) new List<?>[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        if (prerequisites != null) {
            for (int[] prerequisite : prerequisites) {
                graph[prerequisite[1]].add(prerequisite[0]);
            }
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
