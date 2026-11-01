import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Determines whether all courses can be completed using DFS cycle detection. */
public class Problem07 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (numCourses == 0) {
            return true;
        }

        List<Integer>[] graph = buildGraph(numCourses, prerequisites);
        // 0 = unvisited, 1 = currently in DFS path, 2 = fully processed.
        byte[] state = new byte[numCourses];
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
                        return false;
                    }
                    if (state[neighbor] == 0) {
                        state[neighbor] = 1;
                        stack.push(new DfsFrame(neighbor));
                    }
                } else {
                    state[frame.vertex] = 2;
                    stack.pop();
                }
            }
        }

        return true;
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
