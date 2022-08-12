import edu.princeton.cs.algs4.StdOut;

public class Test {
  private static class Apple {
    private int x;

    Apple(int x) {
      this.x = x;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != this.getClass())
        return false;
      Apple aplle = (Apple) o;
      return x == aplle.x;
    }

  }

  public static void main(String[] args) {
    Apple a = new Apple(1);
    Apple b = new Apple(1);
    Apple c = new Apple(2);
    StdOut.println("(a==b):" + (a == b)
        + ", a.equals(b):" + a.equals(b)
        + ", a.equals(c):" + a.equals(c));

    Double d1 = 17.0;
    Double d2 = 17.0;
    StdOut.println(d1 == d2);

    String s = "s";
    StdOut.println(s.hashCode());
  }
}
