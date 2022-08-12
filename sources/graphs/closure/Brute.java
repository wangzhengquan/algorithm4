/**
 * Transitive closure , Brute force Algorithm. Time O(n^4)
 * <p>
 * Example:
 * % java Brute transitive_closure.txt
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public class Brute {
  private int V;
  private int E;
  private boolean[][] matrix;

  public Brute(In in) {
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

      matrix = transitive(matrix);


    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("invalid input format in Digraph constructor", e);
    }
  }

  private boolean[][] transitive1(boolean[][] matrix) {
    boolean[][] A = matrix;
    for (int i = 1; i < V; i++) {
      A = join(A, booleanProduct(A, matrix));
    }
    return A;
  }

  private boolean[][] transitive(boolean[][] matrix) {
    boolean[][] A = matrix;
    boolean[][] B = A;
    for (int i = 1; i < V; i++) {
      A = booleanProduct(A, matrix);
      B = join(B, A);
    }
    return B;
  }

  public boolean[][] join(boolean[][] a, boolean[][] b) {
    if (a.length != b.length || a[0].length != b[0].length) {
      throw new IllegalArgumentException("a and b should have the same number of rows and columns ");
    }
    int m = a.length, n = a[0].length;
    boolean[][] c = new boolean[m][n];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        c[i][j] = a[i][j] || b[i][j];
      }

    return c;
  }

  /**
   * cij =(ai1 & b1j) | (ai2 & b2j) | ··· | (aik & bkj).
   */
  public boolean[][] booleanProduct(boolean[][] a, boolean[][] b) {
    int o = a[0].length;
    if (o != b.length)
      throw new IllegalArgumentException("Column number of a need  equal to the row number of b ");
    int m = a.length, n = b[0].length;
    boolean[][] c = new boolean[m][n];

    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        for (int k = 0; k < o; k++) {
          c[i][j] = c[i][j] || (a[i][k] && b[k][j]);
          if (c[i][j]) break;
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
    Brute ws = new Brute(in);
    ws.showMatrix();
  }

}

