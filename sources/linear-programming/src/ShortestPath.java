/**
 * % java ShortestPath tinyEWD.txt 0 7
 */

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public class ShortestPath {
  public ShortestPath(In in, int s, int t) {
    if (in == null) throw new IllegalArgumentException("argument is null");
    try {
      int n = in.readInt(); //vertices
      if (n < 0) throw new IllegalArgumentException("number of vertices in a Digraph must be non-negative");

      int m = in.readInt(); //edges
      if (m < 0) throw new IllegalArgumentException("Number of edges must be non-negative");
      double[][] A = new double[m + 2][n];
      double[] b = new double[m + 2];
      double[] c = new double[n];
      c[t] = 1;// dist(t)
      
      for (int i = 0; i < m; i++) {
        // dist(w) - dist(w) <= weight(v, w)
        int v = in.readInt();
        int w = in.readInt();
        A[i][v] = -1;
        A[i][w] = 1;
        double weight = in.readDouble();
        b[i] = weight;
      }
      // dist(s)==0  <==> dist(s)>=0  && -dist(s)>=0
      A[m][0] = 1;
      b[m] = 0;
      A[m + 1][0] = -1;
      b[m + 1] = 0;

      Simplex lp;
      try {
        lp = new Simplex(A, b, c);
      } catch (ArithmeticException e) {
        System.out.println(e);
        return;
      }
      StdOut.printf("shortest path from %d to %d  = %f\n", s, t, lp.value());
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("invalid input format in EdgeWeightedDigraph constructor", e);
    }
  }

  public static void main(String[] args) {
    In in = new In(args[0]);

    int s = Integer.parseInt(args[1]);
    int t = Integer.parseInt(args[2]);
    ShortestPath sp = new ShortestPath(in, s, t);
    StdOut.println("-----------compareTo------------");
    compareTo(args);
  }

  public static void compareTo(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
    int s = Integer.parseInt(args[1]);

    // compute shortest paths
    DijkstraSP sp = new DijkstraSP(G, s);


    // print shortest path
    for (int t = 0; t < G.V(); t++) {
      if (sp.hasPathTo(t)) {
        StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
        for (DirectedEdge e : sp.pathTo(t)) {
          StdOut.print(e + "   ");
        }
        StdOut.println();
      } else {
        StdOut.printf("%d to %d         no path\n", s, t);
      }
    }
  }
}
