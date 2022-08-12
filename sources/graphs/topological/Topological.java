/******************************************************************************
 *  Compilation:  javac Topological.java
 *  Execution:    java  Topological filename.txt delimiter
 *  Dependencies: Digraph.java DepthFirstOrder.java DirectedCycle.java
 *                EdgeWeightedDigraph.java EdgeWeightedDirectedCycle.java
 *                SymbolDigraph.java
 *  Data files:   https://algs4.cs.princeton.edu/42digraph/jobs.txt
 *
 *  Compute topological ordering of a DAG or edge-weighted DAG.
 *  Runs in O(E + V) time.
 *
 *  % java Topological jobs.txt "/"
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
import edu.princeton.cs.algs4.StdOut;

public class Topological {
  private Iterable<Integer> order;  // topological order
  private int[] rank;               // rank[v] = rank of vertex v in order

  /**
   * Determines whether the digraph {@code G} has a topological order and, if so,
   * finds such a topological order.
   *
   * @param G the digraph
   */
  public Topological(Digraph G) {
    DirectedCycle finder = new DirectedCycle(G);
    if (!finder.hasCycle()) {
      DepthFirstOrder dfs = new DepthFirstOrder(G);
      order = dfs.reversePost();
      rank = new int[G.V()];
      int i = 0;
      for (int v : order)
        rank[v] = i++;
    }
  }

  /**
   * Determines whether the edge-weighted digraph {@code G} has a topological
   * order and, if so, finds such an order.
   *
   * @param G the edge-weighted digraph
   */
  public Topological(EdgeWeightedDigraph G) {
    EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(G);
    if (!finder.hasCycle()) {
      DepthFirstOrder dfs = new DepthFirstOrder(G);
      order = dfs.reversePost();
    }
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
    test1();
  }

  public static void test1() {
    In in = new In("topological.txt");
    Digraph G = new Digraph(in);
    Topological topological = new Topological(G);
    for (int v : topological.order()) {
      StdOut.print(v + " ");
    }
    StdOut.println();
  }

  public static void test(String[] args) {
    String filename = args[0];
    String delimiter = args[1];
    SymbolDigraph sg = new SymbolDigraph(filename, delimiter);
    Topological topological = new Topological(sg.digraph());
    for (int v : topological.order()) {
      StdOut.println(sg.nameOf(v));
    }
  }

}
