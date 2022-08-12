/**
 * LinearProgramming
 * Reference: CSRS ch29 and Algorithm 4
 * <p>
 * Example
 * maximize 3x_1 + x_2 + 2x_3
 * subject to
 * x_1  + x_2  + x_3    <= 30
 * 2x_1 + 2x_2 + 5x_3   <= 24
 * 4x_1 + x_2  + 2x_3   <= 36
 * x_1,x_2,x_3          >= 0
 * <p>
 * <code>
 * double[] c = {3.0, 1.0, 2.0};
 * double[] b = {30.0, 24.0, 36.0};
 * double[][] A = {
 * {1.0, 1.0, 3.0},
 * {2.0, 2.0, 5.0},
 * {4.0, 1.0, 2.0}
 * };
 * Simplex lp;
 * try {
 * lp = new Simplex(A, b, c);
 * } catch (ArithmeticException e) {
 * System.out.println(e);
 * return;
 * }
 * StdOut.println("value = " + lp.value());
 * double[] x = lp.primal();
 * for (int i = 0; i < x.length; i++)
 * StdOut.println("x[" + i + "] = " + x[i]);
 * </code>
 */

import edu.princeton.cs.algs4.StdOut;

public class Simplex {
  private static final double EPSILON = 1.0E-10;
  private double[][] matrix;   // tableaux
  private int m;          // number of constraints
  private int n;          // number of original variables

  private int[] basis;    // basis[i] = basic variable corresponding to row i
  // only needed to print out solution, not book

  /**
   * Determines an optimal solution to the linear program
   * { max cx : Ax &le; b, x &ge; 0 }, where A is a m-by-n
   * matrix, b is an m-length vector, and c is an n-length vector.
   *
   * @param A the <em>m</em>-by-<em>b</em> matrix
   * @param b the <em>m</em>-length RHS vector
   * @param c the <em>n</em>-length cost vector
   * @throws IllegalArgumentException unless {@code b[i] >= 0} for each {@code i}
   * @throws ArithmeticException      if the linear program is unbounded
   */
  public Simplex(double[][] A, double[] b, double[] c) {

    initializeSimplex(A, b, c);
//    showTableaux();
    solve();
    // check optimality conditions
//    if (!check(A, b, c)) {
//      throw new IllegalArgumentException("check infeasible");
//    }
    assert check(A, b, c);
  }

  // run simplex algorithm starting from initial BFS
  private void solve() {
    while (true) {
      // find entering column q
      int j = bland();
      if (j == -1) break;  // optimal

      // find leaving row p
      int i = minRatioRule(j);
      if (i == -1) throw new ArithmeticException("Linear program is unbounded");

      // pivot
      pivot(i, j);
    }
  }

  private void initializeSimplex(double[][] A, double[] b, double[] c) {
    m = b.length;
    n = c.length;
    int k = 0;
    double minb = b[0];
    for (int i = 1; i < m; i++) {
      if (b[i] < minb) {
        minb = b[i];
        k = i;
      }
    }
    // is the initial basic solution feasible?
    if (b[k] >= 0) {
      initializeMatrix(A, b, c);
      return;
    }
    double[][] A2 = new double[m][n + 1];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        A2[i][j + 1] = A[i][j];
      }

    for (int i = 0; i < m; i++) {
      A2[i][0] = -1.0;
    }

    double[] c2 = new double[n + 1];
    c2[0] = -1.0;
    initializeMatrix(A2, b, c2);
    pivot(k, 0);
    solve();

    if (value() == 0) {
      initializeMatrix(A, b, c);
    } else {
      throw new IllegalArgumentException("infeasible");
    }
  }

  private void initializeSimplex2(double[][] A, double[] b, double[] c) {
    m = b.length;
    n = c.length;
    int k = 0;
    double minb = b[0];
    for (int i = 1; i < m; i++) {
      if (b[i] < minb) {
        minb = b[i];
        k = i;
      }
    }
    // is the initial basic solution feasible?
    if (b[k] >= 0) {
      initializeMatrix(A, b, c);
      return;
    }
    double[][] A2 = new double[m][n + 1];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        A2[i][j + 1] = A[i][j];
      }

    for (int i = 0; i < m; i++) {
      A2[i][0] = -1.0;
    }

    double[] c2 = new double[n + 1];
    c2[0] = -1.0;
    initializeMatrix(A2, b, c2);
    pivot(k, 0);
    solve();


    if (value() == 0) {
      for (int i = 0; i < m; i++)
        if (basis[i] == 0) {
          pivot(i, 1);
        }

      m = b.length;
      n = c.length;
      double[][] newMatrix = new double[m + 1][n + m + 1];
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < n + m + 1; j++) {
          newMatrix[i][j] = matrix[i][j + 1];
        }
      }
      for (int j = 0; j < n; j++)
        newMatrix[m][j] = c[j];

      matrix = newMatrix;
      for (int i = 0; i < m; i++) {
        basis[i] = basis[i] - 1;
      }

      showTableaux();

      for (int i = 0; i < m; i++) {
        int q = basis[i];
        if (q >= n) {
          continue;
        }
        for (int j = 0; j <= m + n; j++) {
          if (j != q)
            matrix[m][j] -= matrix[m][q] * matrix[i][j] / matrix[i][q];
        }
        matrix[m][q] = 0.0;
      }

    } else {
      throw new IllegalArgumentException("infeasible");
    }

    showTableaux();

  }

  private void initializeMatrix(double[][] A, double[] b, double[] c) {
    m = b.length;
    n = c.length;
    matrix = new double[m + 1][n + m + 1];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        matrix[i][j] = A[i][j];
    for (int i = 0; i < m; i++)
      matrix[i][n + i] = 1.0;
    for (int j = 0; j < n; j++)
      matrix[m][j] = c[j];
    for (int i = 0; i < m; i++)
      matrix[i][m + n] = b[i];

    basis = new int[m];
    for (int i = 0; i < m; i++)
      basis[i] = n + i;
  }

  // lowest index of a non-basic column with a positive cost
  private int bland() {
    for (int j = 0; j < m + n; j++)
      if (matrix[m][j] > 0) return j;
    return -1;  // optimal
  }

  // index of a non-basic column with most positive cost
  private int dantzig() {
    int q = 0;
    for (int j = 1; j < m + n; j++)
      if (matrix[m][j] > matrix[m][q]) q = j;

    if (matrix[m][q] <= 0) return -1;  // optimal
    else return q;
  }

  // find row p using min ratio rule (-1 if no such row)
  // (smallest such index if there is a tie)
  private int minRatioRule(int q) {
    int p = -1;
    for (int i = 0; i < m; i++) {
      if (matrix[i][q] <= EPSILON) continue;
      else if (p == -1) p = i;
      else if ((matrix[i][m + n] / matrix[i][q]) < (matrix[p][m + n] / matrix[p][q])) p = i;
    }
    return p;
  }

  // pivot on entry (p, q) using Gauss-Jordan elimination
  private void pivot(int row, int col) {

    // everything but row p and column q
    for (int i = 0; i <= m; i++)
      for (int j = 0; j <= m + n; j++)
        if (i != row && j != col) matrix[i][j] -= matrix[row][j] * matrix[i][col] / matrix[row][col];

    // zero out column q
    for (int i = 0; i <= m; i++)
      if (i != row) matrix[i][col] = 0.0;

    // scale row p
    for (int j = 0; j <= m + n; j++)
      if (j != col) matrix[row][j] /= matrix[row][col];
    matrix[row][col] = 1.0;

    // update basis
    basis[row] = col;
  }

  /**
   * Returns the optimal value of this linear program.
   *
   * @return the optimal value of this linear program
   */
  public double value() {
    return -matrix[m][m + n];
  }

  /**
   * Returns the optimal primal solution to this linear program.
   *
   * @return the optimal primal solution to this linear program
   */
  public double[] primal() {
    double[] x = new double[n];
    for (int i = 0; i < m; i++)
      if (basis[i] < n) x[basis[i]] = matrix[i][m + n];
    return x;
  }

  /**
   * Returns the optimal dual solution to this linear program
   * y_i = -c_n+i if(n+i) contains in N, 0 otherwise
   *
   * @return the optimal dual solution to this linear program
   */
  public double[] dual() {
    double[] y = new double[m];
    for (int i = 0; i < m; i++)
      y[i] = -matrix[m][n + i];
    return y;
  }


  // is the solution primal feasible?
  private boolean isPrimalFeasible(double[][] A, double[] b) {
    double[] x = primal();

    // check that x >= 0
    for (int j = 0; j < x.length; j++) {
      if (x[j] < 0.0) {
        StdOut.println("x[" + j + "] = " + x[j] + " is negative");
        return false;
      }
    }

    // check that Ax <= b
    for (int i = 0; i < m; i++) {
      double sum = 0.0;
      for (int j = 0; j < n; j++) {
        sum += A[i][j] * x[j];
      }
      if (sum > b[i] + EPSILON) {
        StdOut.println("not primal feasible");
        StdOut.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
        return false;
      }
    }
    return true;
  }

  // is the solution dual feasible?
  private boolean isDualFeasible(double[][] A, double[] c) {
    double[] y = dual();

    // check that y >= 0
    for (int i = 0; i < y.length; i++) {
      if (y[i] < 0.0) {
        StdOut.println("y[" + i + "] = " + y[i] + " is negative");
        return false;
      }
    }

    // check that yA >= c
    for (int j = 0; j < n; j++) {
      double sum = 0.0;
      for (int i = 0; i < m; i++) {
        sum += A[i][j] * y[i];
      }
      if (sum < c[j] - EPSILON) {
        StdOut.println("not dual feasible");
        StdOut.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
        return false;
      }
    }
    return true;
  }

  // check that optimal value = cx = yb
  private boolean isOptimal(double[] b, double[] c) {
    double[] x = primal();
    double[] y = dual();
    double value = value();

    // check that value = cx = yb
    double value1 = 0.0;
    for (int j = 0; j < x.length; j++)
      value1 += c[j] * x[j];
    double value2 = 0.0;
    for (int i = 0; i < y.length; i++)
      value2 += y[i] * b[i];
    if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
      StdOut.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
      return false;
    }

    return true;
  }

  private boolean check(double[][] A, double[] b, double[] c) {
    return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
  }

  // print tableaux
  private void showTableaux() {
    StdOut.println("-------------Tableaux----------");
    StdOut.println("m = " + m);
    StdOut.println("n = " + n);
    for (int i = 0; i <= m; i++) {
      for (int j = 0; j <= m + n; j++) {
        if (i < m && basis[i] == j) {
//          StdOut.printf("%7.2f* ", a[i][j]);
          StdOut.print(matrix[i][j] + "* ");
        } else {
          // StdOut.printf("%10.7f ", a[i][j]);
          StdOut.print(matrix[i][j] + " ");
        }
      }
      StdOut.println();
    }
    StdOut.print("value = " + value() + "\n");
    for (int i = 0; i < m; i++)
      StdOut.println("x[" + basis[i] + "] = " + matrix[i][m + n]);
    StdOut.println();
  }


  /**
   * Unit tests the {@code LinearProgramming} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {

//    StdOut.println("----- test 1 --------------------");
//    test1();
//    StdOut.println();
//
//    StdOut.println("----- test 2 --------------------");
//    test2();
//    StdOut.println();


    StdOut.println("----- test 5 --------------------");
    test5();
    StdOut.println();

//    StdOut.println("----- test 5 --------------------");
//    test5();
//    StdOut.println();

  }


  private static void test(double[][] A, double[] b, double[] c) {
    Simplex lp;
    try {
      lp = new Simplex(A, b, c);
    } catch (ArithmeticException e) {
      System.out.println(e);
      return;
    }
    StdOut.println("value = " + lp.value());
    double[] x = lp.primal();
    for (int i = 0; i < x.length; i++)
      StdOut.println("x[" + i + "] = " + x[i]);
    double[] y = lp.dual();
    for (int j = 0; j < y.length; j++)
      StdOut.println("y[" + j + "] = " + y[j]);
  }

  /**
   * maximize 3x_1 + x_2 + 2x_3
   * subject to
   * x_1  + x_2  + x_3    <= 30
   * 2x_1 + 2x_2 + 5x_3   <= 24
   * 4x_1 + x_2  + 2x_3   <= 36
   * x_1,x_2,x_3          >= 0
   */
  private static void test1() {
    double[] c = {3.0, 1.0, 2.0};
    double[] b = {30.0, 24.0, 36.0};
    double[][] A = {
        {1.0, 1.0, 3.0},
        {2.0, 2.0, 5.0},
        {4.0, 1.0, 2.0}
    };

    test(A, b, c);
  }


  // x0 = 12, x1 = 28, opt = 800
  private static void test2() {
    double[] c = {13.0, 23.0};
    double[] b = {480.0, 160.0, 1190.0};
    double[][] A = {
        {5.0, 15.0},
        {4.0, 4.0},
        {35.0, 20.0},
    };
    test(A, b, c);
  }

  private static void test3() {
    double[] c = {2.0, -1.0};
    double[] b = {2.0, -4.0};
    double[][] A = {
        {2.0, -1.0},
        {1.0, -5.0}
    };
    test(A, b, c);
  }

  private static void test4() {
    double[] c = {3.0, 1.0, 2.0};
    double[] b = {30, 24, 36};
    double[][] A = {
        {1, 1, 3},
        {2, 2, 5},
        {4, 1, 2}
    };
    test(A, b, c);
  }

  private static void test5() {
    double[] c = {2, 3, 4};
    double[] b = {50, 80};
    double[][] A = {
        {1, 1, 1},
        {1, 2, 4}
    };
    test(A, b, c);
  }


}
