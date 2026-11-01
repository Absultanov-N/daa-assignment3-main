### DAA Assignment3 - Report and Defense (50 points)

## Overview

This report documents the implementations for all ten graph problems in this repository. The original problem statements and `README.md` have been retained. The required Java class names and public method signatures have also been preserved.

All implementations use Java 17-compatible language features and standard-library collections only. The implementations are designed to avoid recursive DFS for graph sizes where a long path could overflow the Java call stack.

## Problem 1 and 2 - Number of Provinces (10 points)

### Problem

Given an `n x n` adjacency matrix `isConnected`, count the connected components (provinces) in the undirected graph. Each vertex represents a city; an entry of `1` indicates a direct connection.

### Problem 1 - DFS (`src/Problem01.java`)

**Approach.** The implementation scans all vertices. Whenever it finds an unvisited vertex, it starts an iterative depth-first search from that vertex and increments the province count. During the traversal, every city directly connected to the current city is marked visited and pushed onto a stack. Marking a vertex when it is pushed prevents it from being added repeatedly.

**Correctness argument.** Every vertex reachable from a DFS start is visited during that traversal, because the algorithm examines every possible neighbor in the corresponding matrix row. Thus, one traversal visits exactly the connected component containing its start vertex. A new traversal is started only from an unvisited vertex, so it cannot belong to a previously counted component. Every component has at least one vertex and is eventually selected as a start. Therefore, the number of traversals equals the number of provinces.

**Complexity.** Let `n` be the number of cities. The adjacency matrix has `n²` entries and the algorithm scans a row for each visited vertex. Time: `O(n²)`. Auxiliary space: `O(n)` for the visited array and explicit DFS stack.

### Problem 2 - Disjoint Set Union (`src/Problem02.java`)

**Approach.** Initially, every city is in its own set. The implementation examines each pair `(i, j)` with `i < j`. If the matrix indicates a connection, it merges the sets containing the two cities. The DSU uses path compression in `find` and union by size in `union`. The component counter is decremented only when two previously distinct sets are merged.

**Correctness argument.** DSU maintains the invariant that two cities are in the same set exactly when they are connected through the edges processed so far. Each matrix connection merges the corresponding sets, so after all pairs have been examined, each set represents one connected component. The component counter starts at `n` and decreases once for each successful merge. It therefore equals the number of provinces.

**Complexity.** There are `O(n²)` matrix entries to inspect. Each DSU operation takes amortized `O(α(n))` time with path compression and union by size, where `α` is the inverse Ackermann function. Time: `O(n² α(n))`. Auxiliary space: `O(n)`.

**Comparison.** DFS directly explores graph connectivity and is conceptually simple. DSU incrementally merges components and is useful when connectivity is built from an edge list. For this adjacency-matrix input, both must inspect up to `O(n²)` entries; DSU adds a very small amortized factor.

## Problem 3 and 4 - Min Cost Spanning Tree (10 points)

### Problem

Given points in the plane, connect all points with minimum total cost. The cost of connecting two points is their Manhattan distance: `|x1 - x2| + |y1 - y2|`. This is a minimum spanning tree (MST) problem on the complete weighted graph whose vertices are the points.

### Problem 3 - Kruskal (`src/Problem03.java`)

**Approach.** The implementation constructs every undirected edge between pairs of points and calculates its Manhattan weight. It sorts the edges in nondecreasing order. It then scans the sorted edges, accepting an edge only if its endpoints belong to different DSU components. The process stops after `n - 1` edges have been selected.

**Correctness argument.** Kruskal's algorithm processes edges in nondecreasing weight order. At each step, an accepted edge joins two different components and is a safe minimum-weight choice by the cut property of MSTs. DSU rejects edges that would create a cycle. When `n - 1` edges have been accepted, the selected graph is connected and acyclic, hence a spanning tree. By the cut property, it has minimum possible total weight.

**Complexity.** For `n` points, the complete graph has `E = n(n - 1)/2 = O(n²)` edges. Constructing edges takes `O(n²)`, sorting takes `O(E log E) = O(n² log n)`, and DSU processing takes `O(E α(n))`. Overall time: `O(n² log n)`. Auxiliary space: `O(n²)` for the edge array, plus `O(n)` for DSU.

### Problem 4 - Prim (`src/Problem04.java`)

**Approach.** The implementation grows one MST. `minDistance[v]` stores the cheapest edge connecting an unselected point `v` to the current tree. Starting from point `0`, each iteration selects the unvisited point with the smallest `minDistance`, adds that value to the answer, and updates the distances to the remaining points. Because the graph is complete, every unvisited point always has a finite connection candidate after the first selection.

**Correctness argument.** At every iteration, the selected vertices form a connected tree. The unselected vertex with minimum connection cost is joined by a minimum-weight edge crossing the cut between selected and unselected vertices. By the MST cut property, this is a safe edge to add. Repeating this choice until all vertices are selected produces an MST.

**Complexity.** There are `n` iterations. Each iteration scans up to `n` vertices to select the next point and up to `n` vertices to update connection costs. Time: `O(n²)`. Auxiliary space: `O(n)` for `minDistance` and `inMst`.

**Comparison.** Kruskal globally sorts all `O(n²)` edges and uses DSU to avoid cycles. Prim grows one tree and does not materialize or sort the complete edge set. For this dense complete graph, the array-based `O(n²)` Prim implementation uses substantially less auxiliary memory than Kruskal's `O(n²)` edge storage. Both return the same minimum possible total cost.

## Problem 5 and 6 - Software Dependency Cycles (10 points)

### Problem

The input is a directed graph of `n` software modules. A dependency `[a, b]` is a directed edge from module `a` to module `b`. The task is to count strongly connected components (SCCs). A single vertex with no cycle is still one SCC.

### Problem 5 - Kosaraju (`src/Problem05.java`)

**Approach.** The implementation builds the original graph and its transpose (all edges reversed). In the first pass, iterative DFS visits the original graph and records each vertex when its DFS exploration finishes. In the second pass, vertices are processed in reverse finishing order on the transposed graph. Each traversal in this pass identifies one SCC.

**Correctness argument.** The first-pass finishing order arranges SCCs so that, when processed in reverse order on the transpose, a traversal cannot escape from the SCC currently being identified into an unprocessed SCC. Reversing all edges preserves reachability within an SCC while reversing reachability between components. Therefore, each second-pass DFS visits exactly one SCC, and every SCC is visited once. The traversal count is the required answer.

**Complexity.** Building the two adjacency lists takes `O(V + E)`. Each of the two DFS passes visits every vertex and edge at most a constant number of times. Time: `O(V + E)`. Auxiliary space: `O(V + E)` for both graphs, visited state, finishing order, and explicit stacks.

### Problem 6 - Tarjan (`src/Problem06.java`)

**Approach.** Tarjan's algorithm performs one DFS traversal. `discovery[v]` records the discovery index of a vertex, while `low[v]` records the smallest discovery index reachable from that vertex through DFS-tree edges and at most one back edge to a vertex still on the active SCC stack. A vertex is the root of an SCC when `low[v] == discovery[v]`; the vertices are then popped from the SCC stack through that root. The implementation simulates recursive DFS using explicit frames to avoid call-stack overflow.

**Correctness argument.** For each vertex, `low[v]` captures whether its DFS subtree can reach an earlier vertex that remains active. If `low[v] == discovery[v]`, no vertex in the active DFS region below `v` can reach an earlier active vertex outside that region. Consequently, `v` is the root of one SCC, and the vertices popped through `v` are exactly that component. Every vertex is pushed onto and popped from the SCC stack once, so every SCC is counted exactly once.

**Complexity.** Every vertex is discovered once and every directed edge is examined once. Time: `O(V + E)`. Auxiliary space: `O(V + E)` including the adjacency list, index/low arrays, SCC stack, and explicit DFS frames.

**Comparison.** Kosaraju uses two graph passes and stores the transpose. Tarjan uses one DFS pass and does not need a transposed graph. Both run in `O(V + E)` time. The implementations use iterative DFS to remain safe on long paths up to the stated `n = 100,000` constraint.

**Repository note.** `Problem06.md` contains a path typo in its instruction text (it mentions `Problem05.java`). The repository README lists Problem 6 as Tarjan, and the repository contains `src/Problem06.java`; therefore, the Tarjan implementation is placed in `Problem06.java`, keeping the ten problem classes separate.

## Problem 7 and 8 - Course Schedule (10 points)

### Problem

Each prerequisite pair `[course, prerequisite]` represents a directed edge from `prerequisite` to `course`. All courses can be completed if and only if this directed graph contains no cycle.

### Problem 7 - DFS with cycle detection (`src/Problem07.java`)

**Approach.** The implementation uses three states: `0` means unvisited, `1` means currently on the active DFS path, and `2` means fully processed. It explores the graph iteratively. An edge to a state-`1` vertex is a back edge and proves that a directed cycle exists. If all vertices finish without such an edge, the graph is acyclic.

**Correctness argument.** A directed cycle is encountered as an edge to a vertex that is still on the current DFS path, so the algorithm returns `false` whenever a cycle exists. Conversely, if the algorithm finds such an edge, the active DFS path from that vertex to the current vertex, together with the back edge, forms a directed cycle. If no such edge is found, the graph is acyclic and a topological ordering exists; therefore, all courses can be completed.

**Complexity.** Time: `O(V + E)`, because each vertex and edge is processed at most a constant number of times. Auxiliary space: `O(V + E)` for the adjacency list, state array, and explicit DFS stack.

### Problem 8 - Kahn (`src/Problem08.java`)

**Approach.** The implementation calculates the in-degree of each course and places every zero-in-degree course into a queue. It repeatedly removes a course, counts it as processed, and decreases the in-degree of its outgoing neighbors. A neighbor is enqueued when its in-degree becomes zero. The courses can all be completed exactly when the number processed equals `numCourses`.

**Correctness argument.** A zero-in-degree vertex has no prerequisite remaining among the unprocessed vertices, so it can safely be completed next. Removing it and decrementing its outgoing neighbors' in-degrees preserves this property. If all vertices are processed, the resulting removal order is topological and the graph has no cycle. If vertices remain, each remaining vertex has positive in-degree within the remaining graph; following incoming edges must eventually repeat a vertex, which implies a cycle. Thus, the processed-count test correctly determines feasibility.

**Complexity.** Time: `O(V + E)`, since each vertex is enqueued/dequeued at most once and each edge is processed once. Auxiliary space: `O(V + E)` for the graph, in-degree array, and queue.

**Comparison.** DFS detects a back edge through recursion-path state (implemented with explicit stack frames). Kahn repeatedly removes vertices with zero in-degree. Both are linear-time cycle-detection/topological-processing approaches, but use different mechanisms.

## Problem 9 and 10 - Course Schedule II (10 points)

### Problem

Return an ordering of all courses such that every prerequisite appears before the course that depends on it. If no valid ordering exists because the graph contains a cycle, return an empty array.

### Problem 9 - DFS reverse finishing order (`src/Problem09.java`)

**Approach.** The implementation uses the same three-state DFS cycle detection as Problem 7. When a vertex has no remaining unvisited outgoing neighbor, it is marked fully processed and appended to the finishing-order array. If a back edge is found, an empty array is returned. Otherwise, the finishing-order array is reversed to obtain a topological ordering.

**Correctness argument.** In a DAG, for every directed edge `u -> v`, DFS finishes `v` before `u` if `v` is reachable through that edge and is not already completed. Therefore, reversing DFS finishing order places every prerequisite `u` before its dependent course `v`. A back edge is detected exactly when a cycle exists, in which case no topological ordering is possible and the method returns an empty array. Hence, the method returns a valid complete order exactly when one exists.

**Complexity.** Time: `O(V + E)` for graph construction and DFS. Auxiliary space: `O(V + E)` for the graph, state array, order array, and explicit DFS stack.

### Problem 10 - Kahn (`src/Problem10.java`)

**Approach.** The implementation uses the same in-degree queue process as Problem 8, but stores each removed course in an output array. If all `numCourses` vertices are written, it returns that array. Otherwise, it returns an empty array.

**Correctness argument.** A course is added to the output only when its remaining in-degree is zero, meaning all its prerequisites have already been output. Therefore, every prerequisite precedes its dependent course. If all courses are output, the array is a valid topological ordering. If fewer than `numCourses` courses are output, a cycle prevents completion, so returning an empty array is correct.

**Complexity.** Time: `O(V + E)`. Auxiliary space: `O(V + E)` for the adjacency list, in-degree array, queue, and output array.

**Comparison.** Both algorithms return a valid topological ordering when one exists. DFS obtains it by reversing finishing order; Kahn obtains it by repeatedly selecting a zero-in-degree vertex. The order is not necessarily unique, so different valid arrays may be returned for the same input.

## Testing and Validation

A dependency-free Java test runner is included at `tests/Assignment3Test.java`. It covers:

- the examples and basic cases for all ten methods;
- empty/single-vertex cases, fully connected and fully disconnected province matrices,
  disconnected graphs, isolated vertices, self-loops, cycles, duplicate edges, and
  multiple valid topological orders;
- independent BFS validation for province counts;
- independent brute-force spanning-tree validation for small MST instances, including
  duplicate points, negative coordinates, coordinate-boundary cases, fixed-seed random
  cases, and a larger dense stress case;
- independent Floyd-Warshall transitive-closure validation of SCC counts for small
  directed graphs, including self-loops, duplicate edges, disconnected components,
  multiple SCCs, and fixed-seed random cases;
- known-answer large SCC constructions and long directed chains to exercise iterative
  traversals without machine-dependent timing assertions;
- independent reachability-based cycle validation for small course graphs, including
  self-loops, duplicate prerequisites, disconnected graphs, empty graphs, cycles, and
  fixed-seed random cases;
- validation of every successful course ordering for completeness, uniqueness,
  course-ID range, and prerequisite ordering;
- differential cross-checks between DFS and DSU, Kruskal and Prim, Kosaraju and Tarjan,
  and DFS and Kahn. These checks complement the independent oracles but do not replace
  them.

### How to compile and run the tests

From the repository root, using JDK 17 or later:

```bash
mkdir -p out
javac -d out src/*.java tests/Assignment3Test.java
java -cp out Assignment3Test
```

The test runner prints a success message if all assertions pass and throws an `AssertionError` if a check fails. The test runner is separate from the submission classes and does not change their required public interfaces.

### Validation result

The test runner was executed against the submitted source files with JDK 17. The final
run passed **6,651 checks**. This result covers the deterministic test cases,
independent oracles, differential cross-checks, topological-order validation, and
large known-answer constructions described above. Because Problems 05 and 06 expose
only component counts, the SCC tests validate the independent expected count; they do
not claim to observe implementation-level SCC membership labels.

## Conclusion

The ten tasks are implemented with the algorithms required by the assignment: DFS, DSU, Kruskal, Prim, Kosaraju, Tarjan, DFS cycle detection, Kahn's algorithm, DFS reverse finishing order, and Kahn topological sorting. The implementations preserve the provided class names and method signatures. Their correctness follows from graph traversal invariants, DSU component maintenance, the MST cut property, SCC decomposition properties, and topological-order conditions. The complexity of each solution is stated above.


## AI Use Acknowledgment

AI assistance was used during development for algorithm discussion, drafting and reviewing implementation code, identifying edge cases, designing automated tests, debugging, and preparing this report. The student must review the final source code and test results, understand the submitted algorithms, and be able to explain and take responsibility for the final work during the assignment defense, in accordance with the course syllabus.
