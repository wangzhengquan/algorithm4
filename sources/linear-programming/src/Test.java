import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Test {
  /**
   * Unit tests the {@code LinearProgramming} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {

    StdOut.println("----- test 1 --------------------");
    test1();
    StdOut.println();

    StdOut.println("----- test 2 --------------------");
    test2();
    StdOut.println();

    StdOut.println("----- test 3 --------------------");
    test3();
    StdOut.println();

    StdOut.println("----- test 4 --------------------");
    test4();
    StdOut.println();

    StdOut.println("----- test random ---------------");
    int m = Integer.parseInt(args[0]);
    int n = Integer.parseInt(args[1]);
    double[] c = new double[n];
    double[] b = new double[m];
    double[][] A = new double[m][n];
    for (int j = 0; j < n; j++)
      c[j] = StdRandom.uniform(1000);
    for (int i = 0; i < m; i++)
      b[i] = StdRandom.uniform(1000);
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        A[i][j] = StdRandom.uniform(100);
    LinearProgramming lp = new LinearProgramming(A, b, c);
    test(A, b, c);
  }


  private static void test(double[][] A, double[] b, double[] c) {
    LinearProgramming lp;
    try {
      lp = new LinearProgramming(A, b, c);
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

  private static void test1() {
    double[][] A = {
        {-1, 1, 0},
        {1, 4, 0},
        {2, 1, 0},
        {3, -4, 0},
        {0, 0, 1},
    };
    double[] c = {1, 1, 1};
    double[] b = {5, 45, 27, 24, 4};
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

  // unbounded
  private static void test3() {
    double[] c = {2.0, 3.0, -1.0, -12.0};
    double[] b = {3.0, 2.0};
    double[][] A = {
        {-2.0, -9.0, 1.0, 9.0},
        {1.0, 1.0, -1.0, -2.0},
    };
    test(A, b, c);
  }

  // degenerate - cycles if you choose most positive objective function coefficient
  private static void test4() {
    double[] c = {10.0, -57.0, -9.0, -24.0};
    double[] b = {0.0, 0.0, 1.0};
    double[][] A = {
        {0.5, -5.5, -2.5, 9.0},
        {0.5, -1.5, -0.5, 1.0},
        {1.0, 0.0, 0.0, 0.0},
    };
    test(A, b, c);
  }
}
