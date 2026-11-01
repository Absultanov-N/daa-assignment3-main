import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Standalone regression and randomized cross-check tests.
 * Run with: javac -d out src/*.java tests/Assignment3Test.java
 *           java -cp out Assignment3Test
 */
public class Assignment3Test {
    private static int checks = 0;

    public static void main(String[] args) {
        testProvinces();
        testMinimumSpanningTrees();
        testStronglyConnectedComponents();
        testCourseSchedule();
        testIndependentOraclesAndEdgeCases();
        testLongChains();
        System.out.println("All Assignment 3 tests passed (" + checks + " checks).");
    }

    private static void testProvinces() {
        Problem01 dfs = new Problem01();
        Problem02 dsu = new Problem02();

        int[][] sample = {{1,1,0},{1,1,0},{0,0,1}};
        equal(2, dfs.findCircleNum(sample), "Problem 1 sample");
        equal(2, dsu.findCircleNum(sample), "Problem 2 sample");
        equal(1, dfs.findCircleNum(new int[][]{{1}}), "single province DFS");
        equal(1, dsu.findCircleNum(new int[][]{{1}}), "single province DSU");
        equal(0, dfs.findCircleNum(new int[0][0]), "empty matrix DFS");
        equal(0, dsu.findCircleNum(new int[0][0]), "empty matrix DSU");

        Random random = new Random(101);
        for (int trial = 0; trial < 250; trial++) {
            int n = 1 + random.nextInt(18);
            int[][] matrix = new int[n][n];
            for (int i = 0; i < n; i++) {
                matrix[i][i] = 1;
                for (int j = i + 1; j < n; j++) {
                    int edge = random.nextBoolean() ? 1 : 0;
                    matrix[i][j] = matrix[j][i] = edge;
                }
            }
            equal(dfs.findCircleNum(matrix), dsu.findCircleNum(matrix),
                    "random province cross-check " + trial);
        }
    }

    private static void testMinimumSpanningTrees() {
        Problem03 kruskal = new Problem03();
        Problem04 prim = new Problem04();

        int[][] sample = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        equal(20, kruskal.minCostConnectPoints(sample), "Kruskal sample");
        equal(20, prim.minCostConnectPoints(sample), "Prim sample");
        equal(0, kruskal.minCostConnectPoints(new int[][]{{1,1}}), "single point Kruskal");
        equal(0, prim.minCostConnectPoints(new int[][]{{1,1}}), "single point Prim");
        equal(0, kruskal.minCostConnectPoints(new int[0][0]), "empty points Kruskal");
        equal(0, prim.minCostConnectPoints(new int[0][0]), "empty points Prim");

        Random random = new Random(202);
        for (int trial = 0; trial < 150; trial++) {
            int n = 1 + random.nextInt(35);
            int[][] points = new int[n][2];
            for (int i = 0; i < n; i++) {
                points[i][0] = random.nextInt(2001) - 1000;
                points[i][1] = random.nextInt(2001) - 1000;
            }
            equal(kruskal.minCostConnectPoints(points), prim.minCostConnectPoints(points),
                    "random MST cross-check " + trial);
        }
    }

    private static void testStronglyConnectedComponents() {
        Problem05 kosaraju = new Problem05();
        Problem06 tarjan = new Problem06();

        int[][] first = {{0,1},{1,2},{2,0},{2,3},{3,4}};
        equal(3, kosaraju.countDependencyGroups(5, first), "Kosaraju sample 1");
        equal(3, tarjan.countDependencyGroups(5, first), "Tarjan sample 1");
        int[][] second = {{0,1},{1,0},{2,3},{3,2}};
        equal(2, kosaraju.countDependencyGroups(4, second), "Kosaraju sample 2");
        equal(2, tarjan.countDependencyGroups(4, second), "Tarjan sample 2");
        equal(4, kosaraju.countDependencyGroups(4, new int[][]{{0,1},{1,2},{2,3}}),
                "Kosaraju directed chain");
        equal(4, tarjan.countDependencyGroups(4, new int[][]{{0,1},{1,2},{2,3}}),
                "Tarjan directed chain");
        equal(1, kosaraju.countDependencyGroups(1, new int[][]{{0,0}}), "Kosaraju self-loop");
        equal(1, tarjan.countDependencyGroups(1, new int[][]{{0,0}}), "Tarjan self-loop");
        equal(4, kosaraju.countDependencyGroups(4, new int[0][2]), "Kosaraju isolated");
        equal(4, tarjan.countDependencyGroups(4, new int[0][2]), "Tarjan isolated");

        Random random = new Random(303);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(16);
            int m = random.nextInt(55);
            int[][] edges = new int[m][2];
            for (int i = 0; i < m; i++) {
                edges[i][0] = random.nextInt(n);
                edges[i][1] = random.nextInt(n);
            }
            equal(kosaraju.countDependencyGroups(n, edges),
                    tarjan.countDependencyGroups(n, edges),
                    "random SCC cross-check " + trial);
        }
    }

    private static void testCourseSchedule() {
        Problem07 dfs = new Problem07();
        Problem08 kahn = new Problem08();
        Problem09 dfsOrder = new Problem09();
        Problem10 kahnOrder = new Problem10();

        int[][] acyclic = {{1,0},{2,0},{3,1},{3,2}};
        check(dfs.canFinish(4, acyclic), "DFS course sample");
        check(kahn.canFinish(4, acyclic), "Kahn course sample");
        validOrder(4, acyclic, dfsOrder.findOrder(4, acyclic), "DFS order sample");
        validOrder(4, acyclic, kahnOrder.findOrder(4, acyclic), "Kahn order sample");

        int[][] cycle = {{1,0},{0,1}};
        check(!dfs.canFinish(2, cycle), "DFS detects cycle");
        check(!kahn.canFinish(2, cycle), "Kahn detects cycle");
        equal(0, dfsOrder.findOrder(2, cycle).length, "DFS order rejects cycle");
        equal(0, kahnOrder.findOrder(2, cycle).length, "Kahn order rejects cycle");

        check(dfs.canFinish(3, new int[0][2]), "DFS no prerequisites");
        check(kahn.canFinish(3, new int[0][2]), "Kahn no prerequisites");
        validOrder(3, new int[0][2], dfsOrder.findOrder(3, new int[0][2]), "DFS independent courses");
        validOrder(3, new int[0][2], kahnOrder.findOrder(3, new int[0][2]), "Kahn independent courses");
        equal(0, dfsOrder.findOrder(0, new int[0][2]).length, "DFS zero courses");
        equal(0, kahnOrder.findOrder(0, new int[0][2]).length, "Kahn zero courses");

        Random random = new Random(404);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(20);
            int m = random.nextInt(60);
            int[][] prerequisites = new int[m][2];
            for (int i = 0; i < m; i++) {
                prerequisites[i][0] = random.nextInt(n);
                prerequisites[i][1] = random.nextInt(n);
            }

            boolean possibleDfs = dfs.canFinish(n, prerequisites);
            boolean possibleKahn = kahn.canFinish(n, prerequisites);
            check(possibleDfs == possibleKahn, "random feasibility cross-check " + trial);

            int[] orderDfs = dfsOrder.findOrder(n, prerequisites);
            int[] orderKahn = kahnOrder.findOrder(n, prerequisites);
            if (possibleDfs) {
                validOrder(n, prerequisites, orderDfs, "random DFS order " + trial);
                validOrder(n, prerequisites, orderKahn, "random Kahn order " + trial);
            } else {
                equal(0, orderDfs.length, "random DFS cycle result " + trial);
                equal(0, orderKahn.length, "random Kahn cycle result " + trial);
            }
        }
    }

    private static void testLongChains() {
        int n = 100_000;
        int[][] chain = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++) {
            chain[i][0] = i;
            chain[i][1] = i + 1;
        }

        equal(n, new Problem05().countDependencyGroups(n, chain), "long chain Kosaraju");
        equal(n, new Problem06().countDependencyGroups(n, chain), "long chain Tarjan");

        int[][] prerequisites = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++) {
            prerequisites[i][0] = i + 1;
            prerequisites[i][1] = i;
        }
        check(new Problem07().canFinish(n, prerequisites), "long chain DFS feasibility");
        check(new Problem08().canFinish(n, prerequisites), "long chain Kahn feasibility");
        equal(n, new Problem09().findOrder(n, prerequisites).length, "long chain DFS order");
        equal(n, new Problem10().findOrder(n, prerequisites).length, "long chain Kahn order");
    }

    private static void testIndependentOraclesAndEdgeCases() {
        testProvinceOracle();
        testMstOracle();
        testSccOracle();
        testCourseOracle();
    }

    private static void testProvinceOracle() {
        Problem01 dfs = new Problem01();
        Problem02 dsu = new Problem02();

        int[][] fullyConnected = {
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1}
        };
        int[][] fullyDisconnected = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        int[][] multipleComponents = {
                {1, 1, 0, 0, 0, 0},
                {1, 1, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 1}
        };

        assertProvinceOracle(dfs, dsu, new int[0][0], "empty province matrix");
        assertProvinceOracle(dfs, dsu, new int[][]{{1}}, "single-city province matrix");
        assertProvinceOracle(dfs, dsu, fullyConnected, "fully connected provinces");
        assertProvinceOracle(dfs, dsu, fullyDisconnected, "fully disconnected provinces");
        assertProvinceOracle(dfs, dsu, multipleComponents, "multiple province components");

        Random random = new Random(505);
        for (int trial = 0; trial < 120; trial++) {
            int n = 1 + random.nextInt(24);
            int[][] matrix = new int[n][n];
            for (int i = 0; i < n; i++) {
                matrix[i][i] = 1;
                for (int j = i + 1; j < n; j++) {
                    matrix[i][j] = matrix[j][i] = random.nextBoolean() ? 1 : 0;
                }
            }
            assertProvinceOracle(dfs, dsu, matrix, "random province oracle " + trial);
        }
    }

    private static void assertProvinceOracle(Problem01 dfs, Problem02 dsu,
                                             int[][] matrix, String label) {
        int expected = provinceReference(matrix);
        equal(expected, dfs.findCircleNum(matrix), label + " DFS");
        equal(expected, dsu.findCircleNum(matrix), label + " DSU");
    }

    private static int provinceReference(int[][] matrix) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        int components = 0;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            components++;
            visited[start] = true;
            queue.addLast(start);
            while (!queue.isEmpty()) {
                int vertex = queue.removeFirst();
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (matrix[vertex][neighbor] == 1 && !visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.addLast(neighbor);
                    }
                }
            }
        }
        return components;
    }

    private static void testMstOracle() {
        Problem03 kruskal = new Problem03();
        Problem04 prim = new Problem04();

        int[][] boundaryPoints = {
                {-1_000_000, -1_000_000},
                {1_000_000, 1_000_000},
                {-1_000_000, 1_000_000},
                {1_000_000, -1_000_000}
        };
        assertMstOracle(kruskal, prim, boundaryPoints, "coordinate-boundary MST");
        assertMstOracle(kruskal, prim,
                new int[][]{{0, 0}, {0, 0}, {3, 0}, {3, 4}},
                "duplicate-coordinate MST");
        assertMstOracle(kruskal, prim,
                new int[][]{{-5, -5}, {-2, -2}, {0, 0}, {7, 7}},
                "negative-coordinate MST");

        Random random = new Random(606);
        for (int trial = 0; trial < 100; trial++) {
            int n = 2 + random.nextInt(5);
            int[][] points = new int[n][2];
            for (int i = 0; i < n; i++) {
                points[i][0] = random.nextInt(101) - 50;
                points[i][1] = random.nextInt(101) - 50;
            }
            assertMstOracle(kruskal, prim, points, "random brute-force MST " + trial);
        }

        int[][] stress = new int[220][2];
        for (int i = 0; i < stress.length; i++) {
            stress[i][0] = (i * 7919) % 2_000_001 - 1_000_000;
            stress[i][1] = (i * 104729) % 2_000_001 - 1_000_000;
        }
        equal(kruskal.minCostConnectPoints(stress), prim.minCostConnectPoints(stress),
                "dense MST stress cross-check");
    }

    private static void assertMstOracle(Problem03 kruskal, Problem04 prim,
                                        int[][] points, String label) {
        int expected = bruteForceMst(points);
        equal(expected, kruskal.minCostConnectPoints(points), label + " Kruskal");
        equal(expected, prim.minCostConnectPoints(points), label + " Prim");
    }

    private static int bruteForceMst(int[][] points) {
        int n = points.length;
        if (n <= 1) {
            return 0;
        }
        List<MstEdge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long weight = Math.abs((long) points[i][0] - points[j][0])
                        + Math.abs((long) points[i][1] - points[j][1]);
                edges.add(new MstEdge(i, j, weight));
            }
        }
        long[] best = {Long.MAX_VALUE};
        int[] initialParent = new int[n];
        for (int i = 0; i < n; i++) {
            initialParent[i] = i;
        }
        chooseMstEdges(edges, 0, n - 1, initialParent, 0L, best);
        return Math.toIntExact(best[0]);
    }

    private static void chooseMstEdges(List<MstEdge> edges, int index, int needed,
                                       int[] parent, long cost, long[] best) {
        if (needed == 0) {
            if (allConnected(parent)) {
                best[0] = Math.min(best[0], cost);
            }
            return;
        }
        if (edges.size() - index < needed || cost >= best[0]) {
            return;
        }
        for (int i = index; i < edges.size(); i++) {
            MstEdge edge = edges.get(i);
            int[] nextParent = parent.clone();
            if (unionReference(nextParent, edge.from, edge.to)) {
                chooseMstEdges(edges, i + 1, needed - 1, nextParent,
                        cost + edge.weight, best);
            }
        }
    }

    private static boolean unionReference(int[] parent, int first, int second) {
        int rootFirst = findReference(parent, first);
        int rootSecond = findReference(parent, second);
        if (rootFirst == rootSecond) {
            return false;
        }
        parent[rootSecond] = rootFirst;
        return true;
    }

    private static int findReference(int[] parent, int vertex) {
        while (parent[vertex] != vertex) {
            parent[vertex] = parent[parent[vertex]];
            vertex = parent[vertex];
        }
        return vertex;
    }

    private static boolean allConnected(int[] parent) {
        int root = findReference(parent, 0);
        for (int i = 1; i < parent.length; i++) {
            if (findReference(parent, i) != root) {
                return false;
            }
        }
        return true;
    }

    private static final class MstEdge {
        final int from;
        final int to;
        final long weight;

        MstEdge(int from, int to, long weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private static void testSccOracle() {
        Problem05 kosaraju = new Problem05();
        Problem06 tarjan = new Problem06();

        int[][] known = {
                {0, 1}, {1, 2}, {2, 0},
                {2, 3}, {3, 4}, {4, 3},
                {5, 5}, {6, 7}, {6, 7}
        };
        assertSccOracle(kosaraju, tarjan, 8, known, "multiple SCCs");
        assertSccOracle(kosaraju, tarjan, 5,
                new int[][]{{0, 0}, {0, 0}, {1, 2}, {2, 1}},
                "self-loops and duplicate edges");
        assertSccOracle(kosaraju, tarjan, 6, new int[0][2], "disconnected SCCs");

        Random random = new Random(707);
        for (int trial = 0; trial < 140; trial++) {
            int n = 1 + random.nextInt(10);
            int m = random.nextInt(35);
            int[][] edges = new int[m][2];
            for (int i = 0; i < m; i++) {
                edges[i][0] = random.nextInt(n);
                edges[i][1] = random.nextInt(n);
            }
            assertSccOracle(kosaraju, tarjan, n, edges, "random SCC oracle " + trial);
        }

        int n = 50_000;
        int[][] chain = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++) {
            chain[i][0] = i;
            chain[i][1] = i + 1;
        }
        equal(n, kosaraju.countDependencyGroups(n, chain), "large SCC chain Kosaraju");
        equal(n, tarjan.countDependencyGroups(n, chain), "large SCC chain Tarjan");

        int componentSize = 100;
        int components = 300;
        int[][] cycles = new int[components * componentSize][2];
        for (int component = 0; component < components; component++) {
            for (int i = 0; i < componentSize; i++) {
                int index = component * componentSize + i;
                cycles[index][0] = index;
                cycles[index][1] = component * componentSize
                        + (i + 1) % componentSize;
            }
        }
        equal(components, kosaraju.countDependencyGroups(
                components * componentSize, cycles), "large SCC cycles Kosaraju");
        equal(components, tarjan.countDependencyGroups(
                components * componentSize, cycles), "large SCC cycles Tarjan");
    }

    private static void assertSccOracle(Problem05 kosaraju, Problem06 tarjan,
                                        int n, int[][] edges, String label) {
        boolean[][] expected = sccMembershipReference(n, edges);
        int expectedCount = countSccs(expected);
        equal(expectedCount, kosaraju.countDependencyGroups(n, edges),
                label + " Kosaraju oracle");
        equal(expectedCount, tarjan.countDependencyGroups(n, edges),
                label + " Tarjan oracle");
    }

    private static boolean[][] sccMembershipReference(int n, int[][] edges) {
        boolean[][] reach = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            reach[i][i] = true;
        }
        for (int[] edge : edges) {
            reach[edge[0]][edge[1]] = true;
        }
        for (int through = 0; through < n; through++) {
            for (int from = 0; from < n; from++) {
                if (!reach[from][through]) {
                    continue;
                }
                for (int to = 0; to < n; to++) {
                    reach[from][to] |= reach[through][to];
                }
            }
        }
        return reach;
    }

    private static int countSccs(boolean[][] expected) {
        int n = expected.length;
        int expectedCount = 0;
        boolean[] counted = new boolean[n];
        for (int vertex = 0; vertex < n; vertex++) {
            if (!counted[vertex]) {
                expectedCount++;
                for (int other = 0; other < n; other++) {
                    if (expected[vertex][other] && expected[other][vertex]) {
                        counted[other] = true;
                    }
                }
            }
        }
        return expectedCount;
    }

    private static void testCourseOracle() {
        Problem07 dfs = new Problem07();
        Problem08 kahn = new Problem08();
        Problem09 dfsOrder = new Problem09();
        Problem10 kahnOrder = new Problem10();

        int[][] selfLoop = {{2, 2}};
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 4, selfLoop,
                "course self-loop");
        int[][] duplicates = {{1, 0}, {1, 0}, {2, 1}, {2, 1}};
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 4, duplicates,
                "duplicate prerequisites");
        int[][] disconnected = {{1, 0}, {3, 2}};
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 5, disconnected,
                "disconnected courses");
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 0, new int[0][2],
                "empty course graph");
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 5, new int[0][2],
                "courses without prerequisites");
        assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, 4,
                new int[][]{{1, 0}, {2, 1}, {0, 2}},
                "three-course cycle");

        Random random = new Random(808);
        for (int trial = 0; trial < 160; trial++) {
            int n = random.nextInt(13);
            int m = n == 0 ? 0 : random.nextInt(40);
            int[][] prerequisites = new int[m][2];
            for (int i = 0; i < m; i++) {
                prerequisites[i][0] = random.nextInt(n);
                prerequisites[i][1] = random.nextInt(n);
            }
            assertCourseOracle(dfs, kahn, dfsOrder, kahnOrder, n, prerequisites,
                    "random course oracle " + trial);
        }
    }

    private static void assertCourseOracle(Problem07 dfs, Problem08 kahn,
                                            Problem09 dfsOrder, Problem10 kahnOrder,
                                            int n, int[][] prerequisites, String label) {
        boolean expected = courseCycleFreeReference(n, prerequisites);
        check(expected == dfs.canFinish(n, prerequisites), label + " DFS feasibility");
        check(expected == kahn.canFinish(n, prerequisites), label + " Kahn feasibility");
        int[] orderDfs = dfsOrder.findOrder(n, prerequisites);
        int[] orderKahn = kahnOrder.findOrder(n, prerequisites);
        if (expected) {
            validateOrder(n, prerequisites, orderDfs, label + " DFS order");
            validateOrder(n, prerequisites, orderKahn, label + " Kahn order");
        } else {
            equal(0, orderDfs.length, label + " DFS cyclic order");
            equal(0, orderKahn.length, label + " Kahn cyclic order");
        }
    }

    private static boolean courseCycleFreeReference(int n, int[][] prerequisites) {
        boolean[][] reach = new boolean[n][n];
        for (int[] pair : prerequisites) {
            reach[pair[1]][pair[0]] = true;
        }
        for (int through = 0; through < n; through++) {
            for (int from = 0; from < n; from++) {
                if (!reach[from][through]) {
                    continue;
                }
                for (int to = 0; to < n; to++) {
                    reach[from][to] |= reach[through][to];
                }
            }
        }
        for (int vertex = 0; vertex < n; vertex++) {
            if (reach[vertex][vertex]) {
                return false;
            }
        }
        return true;
    }

    private static void validateOrder(int n, int[][] prerequisites, int[] order,
                                      String label) {
        equal(n, order.length, label + " complete length");
        int[] position = new int[n];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            int course = order[i];
            check(course >= 0 && course < n, label + " course range");
            check(position[course] == -1, label + " unique course IDs");
            position[course] = i;
        }
        for (int course = 0; course < n; course++) {
            check(position[course] >= 0, label + " complete course IDs");
        }
        for (int[] pair : prerequisites) {
            check(position[pair[1]] < position[pair[0]],
                    label + " prerequisite ordering");
        }
    }

    private static void validOrder(int n, int[][] prerequisites, int[] order, String label) {
        equal(n, order.length, label + " length");
        int[] position = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < order.length; i++) {
            int course = order[i];
            check(course >= 0 && course < n, label + " course range");
            check(!seen[course], label + " no duplicate course");
            seen[course] = true;
            position[course] = i;
        }
        for (int[] pair : prerequisites) {
            int course = pair[0];
            int prerequisite = pair[1];
            check(position[prerequisite] < position[course], label + " prerequisite order");
        }
    }

    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void equal(int expected, int actual, String label) {
        checks++;
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
