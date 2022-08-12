import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class Test {

  static int scale = 1 << 20;

  public static int[] test() {
    int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int[] b = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int[] c = a;
    StdOut.println("a=" + a);
    StdOut.println("b=" + b);
    StdOut.println("c=" + c);
    return a;
  }

  public static double test1() {
    double a = 1;
    for (int i = 0; i < scale; i++) {
      a = a * StdRandom.uniform(1.0, 10.0) / StdRandom.uniform(1.0, 10.0);
    }
    for (int i = 0; i < scale; i++) {
      a = a * StdRandom.uniform(1.0, 10.0) / StdRandom.uniform(1.0, 10.0);
    }
    return a;
  }

  public static double test2() {
    double a = 1;
    for (int i = 0; i < scale; i++) {
      a = a * StdRandom.uniform(1.0, 10.0) / StdRandom.uniform(1.0, 10.0);
      a = a * StdRandom.uniform(1.0, 10.0) / StdRandom.uniform(1.0, 10.0);
    }
    return a;
  }

  public static double test3() {
    double a = 1;
    for (int i = 0; i < scale; i++) {
      a = a * StdRandom.uniform(1.0, 10.0) / StdRandom.uniform(1.0, 10.0);
    }
    return a;
  }

  public static void main(String[] args) {
    Stopwatch watch = new Stopwatch();
    test1();
    System.out.printf("time elapsed %f\n", watch.elapsedTime());

    Stopwatch watch2 = new Stopwatch();
    test2();
    System.out.printf("time elapsed %f\n", watch2.elapsedTime());

    Stopwatch watch3 = new Stopwatch();
    test3();
    System.out.printf("time elapsed %f\n", watch3.elapsedTime());
  }
}
