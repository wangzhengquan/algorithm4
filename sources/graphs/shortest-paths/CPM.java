/******************************************************************************
 *  Compilation:  javac CPM.java
 *  Execution:    java CPM < input.txt
 *  Dependencies: EdgeWeightedDigraph.java AcyclicDigraphLP.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/44sp/jobsPC.txt
 *
 *  Critical path method.
 *
 *  % java CPM < jobsPC.txt
 *   job   start  finish
 *  --------------------
 *     0     0.0    41.0
 *     1    41.0    92.0
 *     2   123.0   173.0
 *     3    91.0   127.0
 *     4    70.0   108.0
 *     5     0.0    45.0
 *     6    70.0    91.0
 *     7    41.0    73.0
 *     8    91.0   123.0
 *     9    41.0    70.0
 *  Finish time:   173.0
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code CPM} class provides a client that solves the
 * parallel precedence-constrained job scheduling problem
 * via the <em>critical path method</em>. It reduces the problem
 * to the longest-paths problem in edge-weighted DAGs.
 * It builds an edge-weighted digraph (which must be a DAG)
 * from the job-scheduling problem specification,
 * finds the longest-paths tree, and computes the longest-paths
 * lengths (which are precisely the start times for each job).
 * <p>
 * This implementation uses {@link AcyclicLP} to find a longest
 * path in a DAG.
 * The program takes &Theta;(<em>V</em> + <em>E</em>) time in
 * the worst case, where <em>V</em> is the number of jobs and
 * <em>E</em> is the number of precedence constraints.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class CPM {

  // this class cannot be instantiated
  private CPM() {
  }
//  Iterable<DirectedEdge> pathTo

  private static String pathsStr(Iterable<DirectedEdge> paths) {
    StringBuilder buf = new StringBuilder();
    DirectedEdge lastPath = null;
    for (DirectedEdge path : paths) {
      buf.append(path.from() + "->");
      lastPath = path;
    }
    buf.append(lastPath.to());
    return buf.toString();
  }

  /**
   * Reads the precedence constraints from standard input
   * and prints a feasible schedule to standard output.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    int N = StdIn.readInt();
    StdIn.readLine();
    EdgeWeightedDigraph G;
    G = new EdgeWeightedDigraph(2 * N + 2);
    int s = 2 * N, t = 2 * N + 1;
    for (int i = 0; i < N; i++) {
      String[] a = StdIn.readLine().split("\\s+");
      double duration = Double.parseDouble(a[0]);
      G.addEdge(new DirectedEdge(i, i + N, duration));
      G.addEdge(new DirectedEdge(s, i, 0.0));
      G.addEdge(new DirectedEdge(i + N, t, 0.0));
      for (int j = 1; j < a.length; j++) {
        int successor = Integer.parseInt(a[j]);
        G.addEdge(new DirectedEdge(i + N, successor, 0.0));
      }
    }
    AcyclicLP lp = new AcyclicLP(G, s);
    StdOut.println("Job : Start times : Path");
    for (int i = 0; i < N; i++)
      StdOut.printf("%4d: %5.1f : %s\n", i, lp.distTo(i), pathsStr(lp.pathTo(i)));
    StdOut.printf("Finish time: %5.1f\n", lp.distTo(t));
  }

}
