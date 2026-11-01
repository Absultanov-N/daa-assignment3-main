import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Returns a valid course order using Kahn's topological-sort algorithm. */
public class Problem10 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        if (numCourses == 0) {
            return new int[0];
        }

        List<Integer>[] graph = buildGraph(numCourses, prerequisites);
        int[] indegree = new int[numCourses];

        if (prerequisites != null) {
            for (int[] prerequisite : prerequisites) {
                indegree[prerequisite[0]]++;
            }
        }

        Deque<Integer> queue = new ArrayDeque<>();
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue.addLast(course);
            }
        }

        int[] order = new int[numCourses];
        int orderSize = 0;

        while (!queue.isEmpty()) {
            int course = queue.removeFirst();
            order[orderSize++] = course;

            for (int next : graph[course]) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.addLast(next);
                }
            }
        }

        return orderSize == numCourses ? order : new int[0];
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
}
