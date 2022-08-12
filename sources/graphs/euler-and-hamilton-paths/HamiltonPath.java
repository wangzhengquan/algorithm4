/**
 * Execution:
 * % java HamiltonPath hamilton_cycle.txt
 * <p>
 * Data file: hamilton_cycle.txt hamilton_path.txt no_hamilton_path.txt
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class HamiltonPath {
  private boolean[] marked;
  private int count = 0;
  private int cycleCount = 0;
  private int[] edgeTo;        // edgeTo[v] = last edge on s-v path
  private List<Queue<Integer>> paths = new ArrayList<>();

  public HamiltonPath(Graph G) {
    edgeTo = new int[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      dfs(G, v, 1);
  }

  private void dfs(Graph G, int v, int depth) {
    marked[v] = true;
    if (depth == G.V()) {
      // found one
      count++;
      Stack<Integer> stack = new Stack<Integer>();
      int x, i;
      for (i = 0, x = v; i < depth; x = edgeTo[x], i++)
        stack.push(x);

      Queue<Integer> path = new Queue<>();
      for (int y : stack) {
        path.enqueue(y);
      }
      //check hamilton cycle
      for (int w : G.adj(x)) {
        if (w == v) {
          cycleCount++;
          path.enqueue(x);
        }
      }
      paths.add(path);
    }

    for (int w : G.adj(v)) {
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, w, depth + 1);
      }
    }

    marked[v] = false; // clean up
  }

  public List<Queue<Integer>> getPaths() {
    return paths;
  }

  public void showPath(Queue<Integer> path) {
    for (int v : path) {
      StdOut.print(v + " ");
    }
    StdOut.println();
  }

  public boolean hasHamiltonPath() {
    return count > 0;
  }

  public boolean hasHamiltonCycle() {
    return cycleCount > 0;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    HamiltonPath hp = new HamiltonPath(G);
    if (!hp.hasHamiltonPath()) {
      StdOut.println("No Hamilton Path.");
    } else {
      for (Queue<Integer> path : hp.getPaths()) {
        hp.showPath(path);
      }
    }
  }
}
