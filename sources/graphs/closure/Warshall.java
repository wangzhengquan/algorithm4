import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

/**
 * Transitive closure , Warshall Algorithm. Time O(n^3)
 * <p>
 * Example:
 * % java Warshall transitive_closure.txt
 */
public class Warshall {
  private int V;
  private int E;
  private boolean[][] matrix;

  public Warshall(In in) {
    if (in == null) throw new IllegalArgumentException("argument is null");
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException("number of vertices in a Digraph must be non-negative");
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException("number of edges in a Digraph must be non-negative");
      matrix = new boolean[V][V];
      for (int i = 0; i < E; i++) {
//        matrix[i][i] = true;
        int v = in.readInt();
        int w = in.readInt();
        matrix[v][w] = true;
      }

      transitive(matrix);


    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("invalid input format in Digraph constructor", e);
    }
  }

  private boolean[][] transitive(boolean[][] c) {
    int n = matrix.length;
    if (n != matrix[0].length)
      throw new IllegalArgumentException("The number of rows should equal the number of columns");

    for (int k = 0; k < n; k++)
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++) {
          c[i][j] = c[i][j] || (c[i][k] && c[k][j]);
        }
    return c;
  }

  public void showMatrix() {
    // print header
    StdOut.print("     ");
    for (int v = 0; v < V; v++)
      StdOut.printf("%3d", v);
    StdOut.println();
    StdOut.println("--------------------------------------------");

    // print transitive closure
    for (int v = 0; v < V; v++) {
      StdOut.printf("%3d: ", v);
      for (int w = 0; w < V; w++) {
//        if (matrix[v][w]) StdOut.printf("  1");
//        else StdOut.printf("  0");
        if (matrix[v][w]) StdOut.printf("  T");
        else StdOut.printf("   ");
      }
      StdOut.println();
    }

  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    Warshall ws = new Warshall(in);
    ws.showMatrix();
  }
}
