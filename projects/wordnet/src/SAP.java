import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class SAP {
  private Digraph G;
  private BreadthFirstDirectedPaths bfs1;
  private BreadthFirstDirectedPaths bfs2;
  private int root;

  // constructor takes a digraph (not necessarily a DAG)
  public SAP(Digraph graph) {
    if (graph == null)
      throw new IllegalArgumentException("SAP construct argument should not be null");

    this.G = new Digraph(graph);
  }

  // length of shortest ancestral path between v and w; -1 if no such path
  public int length(int v, int w) {
//    if (v == w)
//      return 0;
    int minV = this.ancestor(v, w);
    if (minV == -1)
      return -1;

    return bfs1.distTo(minV) + bfs2.distTo(minV);
  }

  // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
  public int ancestor(int x, int y) {
    bfs1 = new BreadthFirstDirectedPaths(this.G, x);
    bfs2 = new BreadthFirstDirectedPaths(this.G, y);
    boolean[] marked = new boolean[G.V()];

    int dist;
    int minDist = Integer.MAX_VALUE;
    int minV = -1;
    Queue<Integer> q = new Queue<Integer>();
    marked[x] = true;
    q.enqueue(x);
    while (!q.isEmpty()) {
      int v = q.dequeue();
      if (bfs2.hasPathTo(v)) {
        dist = bfs1.distTo(v) + bfs2.distTo(v);
        if (dist < minDist) {
          minDist = dist;
          minV = v;
        }
      }
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
    return minV;
  }

  // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
  public int length(Iterable<Integer> listv, Iterable<Integer> listw) {
    if (listv == null || listw == null)
      throw new IllegalArgumentException("length argument should not be null");
    int minV = this.ancestor(listv, listw);
    if (minV == -1)
      return -1;

    return bfs1.distTo(minV) + bfs2.distTo(minV);
  }

  // a common ancestor that participates in shortest ancestral path; -1 if no such path
  public int ancestor(Iterable<Integer> listv, Iterable<Integer> listw) {
    if (listv == null || listw == null)
      throw new IllegalArgumentException("ancestor argument should not be null");
    if (!listv.iterator().hasNext() || !listw.iterator().hasNext()) {
      return -1;
    }
    bfs1 = new BreadthFirstDirectedPaths(this.G, listv);
    bfs2 = new BreadthFirstDirectedPaths(this.G, listw);
    boolean[] marked = new boolean[G.V()];

    int dist;
    int minDist = Integer.MAX_VALUE;
    int minV = -1;

    Queue<Integer> q = new Queue<Integer>();
    for (Integer s : listv) {
      marked[s] = true;
      q.enqueue(s);
    }
    while (!q.isEmpty()) {
      Integer v = q.dequeue();
      if (v == null)
        throw new IllegalArgumentException("ancestor arguments contains null item");
      if (bfs2.hasPathTo(v)) {
        dist = bfs1.distTo(v) + bfs2.distTo(v);
        if (dist < minDist) {
          minDist = dist;
          minV = v;
        }
      }
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
    return minV;
  }

//  private void validateVertex(int v) {
//    int V = G.V();
//    if (v < 0 || v >= V)
//      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
//  }

  public static void main(String[] args) {
//    testIllegalArgument();
    testAncestorInSet();
//    testAncestor();

//    int v = 7, w = 2;
//    int length = sap.length(v, w);
//    int ancestor = sap.ancestor(v, w);
//    StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//

  }

  private static void testIllegalArgument() {
    In in = new In("digraph25.txt");
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);
    sap.ancestor(null, null);
  }

  private static void testAncestorInSet() {
    In in = new In("digraph25.txt");
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);
    List<Integer> A = new ArrayList<>();
    A.add(13);
    A.add(23);
    A.add(24);
    List<Integer> B = new ArrayList<>();
    B.add(6);
    B.add(16);
    B.add(17);
    int ancestor = sap.ancestor(A, B);
    int length = sap.length(A, B);
    StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
  }

  /**
   * 3 11
   * length = 4, ancestor = 1
   * 9 12
   * length = 3, ancestor = 5
   * 7 2
   * length = 4, ancestor = 0
   * 1 6
   * length = -1, ancestor = -1
   */
  private static void testAncestor() {
    In in = new In("digraph1.txt");
    In stdin = new In("input1.txt");
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);
    while (!stdin.isEmpty()) {
      int v = stdin.readInt();
      int w = stdin.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }

  private static void testAncestorInteraction(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);
    while (!StdIn.isEmpty()) {
      int v = StdIn.readInt();
      int w = StdIn.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }
}
