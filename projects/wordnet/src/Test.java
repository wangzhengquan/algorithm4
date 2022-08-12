import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Test {
  public Iterable<Integer> list() {
    StdOut.println("---list--");
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 10; i++)
      list.add(i);
    return list;
  }

  public static void main(String[] args) {
    Test t = new Test();
    for (int x : t.list()) {
      StdOut.println(x);
    }
  }
}
