/******************************************************************************
 *  Compilation:  javac TopologicalDFS.java
 *  Execution:    java  Topological filename.txt delimiter
 *  Dependencies: Digraph.java  DirectedCycle.java
 *                EdgeWeightedDigraph.java EdgeWeightedDirectedCycle.java
 *                SymbolDigraph.java
 *  Data files:   https://algs4.cs.princeton.edu/42digraph/jobs.txt
 *
 *  Compute topological ordering of a DAG or edge-weighted DAG.
 *  Runs in O(E + V) time.
 *
 *  % java TopologicalDFS jobs.txt "/"
 *  Calculus
 *  Linear Algebra
 *  Introduction to CS
 *  Advanced Programming
 *  Algorithms
 *  Theoretical CS
 *  Artificial Intelligence
 *  Robotics
 *  Machine Learning
 *  Neural Networks
 *  Databases
 *  Scientific Computing
 *  Computational Biology
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class TopologicalDFS {
  private Stack<Integer> order;  // topological order
  private int[] rank;               // rank[v] = rank of vertex v in order
  private boolean[] marked;          // marked[v] = has v been marked in dfs?

  /**
   * Determines whether the digraph {@code G} has a topological order and, if so,
   * finds such a topological order.
   *
   * @param G the digraph
   */
  public TopologicalDFS(Digraph G) {
    DirectedCycle finder = new DirectedCycle(G);
    if (!finder.hasCycle()) {
      order = new Stack<Integer>();
      marked = new boolean[G.V()];
      for (int v = 0; v < G.V(); v++)
        if (!marked[v]) dfs(G, v);

      rank = new int[G.V()];
      int i = 0;
      for (int v : order)
        rank[v] = i++;
    }
  }

  // run DFS in digraph G from vertex v and compute preorder/postorder
  private void dfs(Digraph G, int v) {
    marked[v] = true;
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
    order.push(v);
  }

  /**
   * Returns a topological order if the digraph has a topologial order,
   * and {@code null} otherwise.
   *
   * @return a topological order of the vertices (as an interable) if the
   * digraph has a topological order (or equivalently, if the digraph is a DAG),
   * and {@code null} otherwise
   */
  public Iterable<Integer> order() {
    return order;
  }

  /**
   * Does the digraph have a topological order?
   *
   * @return {@code true} if the digraph has a topological order (or equivalently,
   * if the digraph is a DAG), and {@code false} otherwise
   */
  public boolean hasOrder() {
    return order != null;
  }

  /**
   * Does the digraph have a topological order?
   *
   * @return {@code true} if the digraph has a topological order (or equivalently,
   * if the digraph is a DAG), and {@code false} otherwise
   * @deprecated Replaced by {@link #hasOrder()}.
   */
  @Deprecated
  public boolean isDAG() {
    return hasOrder();
  }

  /**
   * The the rank of vertex {@code v} in the topological order;
   * -1 if the digraph is not a DAG
   *
   * @param v the vertex
   * @return the position of vertex {@code v} in a topological order
   * of the digraph; -1 if the digraph is not a DAG
   * @throws IllegalArgumentException unless {@code 0 <= v < V}
   */
  public int rank(int v) {
    validateVertex(v);
    if (hasOrder()) return rank[v];
    else return -1;
  }

  // throw an IllegalArgumentException unless {@code 0 <= v < V}
  private void validateVertex(int v) {
    int V = rank.length;
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
  }

  /**
   * Unit tests the {@code Topological} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    test(args);
  }

  public static void test1() {
    In in = new In("topological.txt");
    Digraph G = new Digraph(in);
    TopologicalDFS TopologicalDFS = new TopologicalDFS(G);
    for (int v : TopologicalDFS.order()) {
      StdOut.print(v + " ");
    }
    StdOut.println();

    Topological topological = new Topological(G);
    for (int v : topological.order()) {
      StdOut.print(v + " ");
    }
    StdOut.println();

  }

  // % java TopologicalDFS jobs.txt "/"
  public static void test(String[] args) {
    String filename = args[0];
    String delimiter = args[1];
    SymbolDigraph sg = new SymbolDigraph(filename, delimiter);

    TopologicalDFS topologicalDFS = new TopologicalDFS(sg.digraph());
    for (int v : topologicalDFS.order()) {
      StdOut.println(sg.nameOf(v));
    }
    StdOut.println("----------compare to---------------");
    Topological topological = new Topological(sg.digraph());
    for (int v : topological.order()) {
      StdOut.println(sg.nameOf(v));
    }
  }

}

