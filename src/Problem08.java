import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Determines course feasibility using Kahn's topological-sort algorithm. */
public class Problem08 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (numCourses == 0) {
            return true;
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

        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.removeFirst();
            processed++;

            for (int next : graph[course]) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.addLast(next);
                }
            }
        }

        return processed == numCourses;
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
