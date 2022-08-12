/**
 * % java MaxFlow tinyFN.txt 0 5
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class MaxFlow {
  private class Edge {
    // to deal with floating-point roundoff errors
    private static final double FLOATING_POINT_EPSILON = 1E-10;

    private final int from;             // from
    private final int to;             // to
    private final double capacity;   // capacity
    private double flow;             // flow
    private int identity;

    public Edge(int v, int w, double capacity, int identity) {
      if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
      if (w < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
      if (!(capacity >= 0.0)) throw new IllegalArgumentException("Edge capacity must be non-negative");

      this.from = v;
      this.to = w;
      this.capacity = capacity;
      this.flow = 0.0;
      this.identity = identity;
    }

    public int from() {
      return from;
    }

    public int to() {
      return to;
    }

    public int identity() {
      return identity;
    }

    public double capacity() {
      return capacity;
    }

    public String toString() {
      return from + "->" + to;
    }
  }

  private Bag<Edge>[] adj;

  public MaxFlow(In in, int s, int t) {
    int V = in.readInt();
    adj = (Bag<Edge>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new Bag<Edge>();
    }
    int E = in.readInt();
    Edge[] edges = new Edge[E];
    for (int i = 0; i < E; i++) {
      int v = in.readInt();
      int w = in.readInt();
      double capacity = in.readDouble();
      Edge e = new Edge(v, w, capacity, i);
      addEdge(e);
      edges[i] = e;
    }

    double[][] A = new double[2 * V + E][E];
    double[] b = new double[2 * V + E];
    double[] c = new double[E];
    for (Edge edge : adj[s]) {
      // flow in S minus flow out S
      if (s == edge.from()) {
        c[edge.identity()] = 1.0;
      } else {
        c[edge.identity()] = -1.0;
      }
    }

    int m = 0;
    for (int v = 0; v < V; v++) {
      for (Edge edge : adj[v]) {
        if (v == edge.from()) {
          // each flow < capacity
          A[m][edge.identity()] = 1.0;
          b[m] = edge.capacity();
          m++;
        }
      }
    }

    for (int v = 0; v < V; v++) {
      if (v != s && v != t && adj[v].size() > 0) {
        // flow in = flow out
        for (Edge edge : adj[v]) {
          if (v == edge.from()) {
            A[m][edge.identity()] = -1.0;
            A[m + 1][edge.identity()] = 1.0;
          } else {
            A[m][edge.identity()] = 1.0;
            A[m + 1][edge.identity()] = -1.0;
          }
          b[m] = 0;
          b[m + 1] = 0;
        }
        m += 2;
      }
    }


    Simplex lp;
    try {
      lp = new Simplex(A, b, c);
    } catch (ArithmeticException e) {
      System.out.println(e);
      return;
    }
    StdOut.printf("Max flow from %d to %d  = %f\n", s, t, lp.value());
    double[] x = lp.primal();
    for (int i = 0; i < x.length; i++)
      StdOut.println(edges[i] + " " + x[i] + "/" + edges[i].capacity());
//    StdOut.println("x[" + i + "] = " + x[i]);

  }

  private void addEdge(Edge e) {
    int v = e.from();
    int w = e.to();
    adj[v].add(e);
    adj[w].add(e);
//    E++;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);

    int s = Integer.parseInt(args[1]);
    int t = Integer.parseInt(args[2]);
    MaxFlow mf = new MaxFlow(in, s, t);
    compareTo(args);
  }

  public static void compareTo(String[] args) {
    StdOut.println("-----------compareTo------------");
    In in = new In(args[0]);
    int s = Integer.parseInt(args[1]);
    int t = Integer.parseInt(args[2]);
    FlowNetwork G = new FlowNetwork(in);
    // compute maximum flow and minimum cut
    FordFulkerson maxflow = new FordFulkerson(G, s, t);
    StdOut.printf("Max flow from %d to %d  = %f\n", s, t, maxflow.value());
    for (int v = 0; v < G.V(); v++) {
      for (FlowEdge e : G.adj(v)) {
        if ((v == e.from()) && e.flow() > 0)
          StdOut.println("   " + e);
      }
    }

    // print min-cut
    StdOut.print("Min cut: ");
    for (int v = 0; v < G.V(); v++) {
      if (maxflow.inCut(v)) StdOut.print(v + " ");
    }
    StdOut.println();

  }
}
