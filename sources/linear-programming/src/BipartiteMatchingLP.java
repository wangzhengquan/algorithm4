import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BipartiteMatching;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BipartiteMatchingLP {

  private class Edge {
    // to deal with floating-point roundoff errors
    private static final double FLOATING_POINT_EPSILON = 1E-10;

    private final int from;             // from
    private final int to;             // to
    private int identity;

    public Edge(int v, int w, int identity) {
      if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
      if (w < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
      this.from = v;
      this.to = w;
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

    public String toString() {
      return from + "->" + to;
    }

  }

  private Bag<Edge>[] adj;

  public BipartiteMatchingLP(In in) {
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
      Edge e = new Edge(v, w, i);
      addEdge(e);
      edges[i] = e;
    }
    double[][] A = new double[V][E];
    double[] b = new double[V];
    double[] c = new double[E];
    for (int j = 0; j < E; j++) {
      // all person-job combination
      c[j] = 1;
    }
    int m = 0;
    for (int v = 0; v < V; v++) {
      //at most one job per person & at most one person per job
      for (Edge e : adj[v]) {
        A[m][e.identity()] = 1;
      }
      b[m] = 1;
      m++;

    }
    Simplex lp;
    try {
      lp = new Simplex(A, b, c);
    } catch (ArithmeticException e) {
      System.out.println(e);
      return;
    }
    StdOut.printf("Number of edges in max matching = %f\n", lp.value());
    StdOut.println("Matched pairs: ");
    double[] x = lp.primal();
    for (int i = 0; i < x.length; i++) {
//      StdOut.println("x[" + i + "] = " + x[i]);
      if (x[i] == 1) {
        StdOut.println(edges[i]);
      }
    }
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

    BipartiteMatchingLP mf = new BipartiteMatchingLP(in);
    compareTo(args);
  }

  public static void compareTo(String[] args) {
    StdOut.println("-----------compareTo------------");
    StdOut.println("The number of matching is same as the up method but the combinations may be difference.");
    In in = new In(args[0]);
    Graph G = new Graph(in);

    BipartiteMatching matching = new BipartiteMatching(G);

    // print maximum matching
    StdOut.printf("Number of edges in max matching        = %d\n", matching.size());
    StdOut.printf("Graph has a perfect matching           = %b\n", matching.isPerfect());
    StdOut.println();

    if (G.V() >= 1000) return;

    StdOut.print("Matched pairs: ");
    for (int v = 0; v < G.V(); v++) {
      int w = matching.mate(v);
      if (matching.isMatched(v) && v < w)  // print each edge only once
        StdOut.print(v + "-" + w + " ");
    }
    StdOut.println();
  }
}
