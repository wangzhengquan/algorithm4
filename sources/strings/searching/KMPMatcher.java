/**
 * reference CLRS
 */

import edu.princeton.cs.algs4.In;

public class KMPMatcher {
  String pattern;
  int[] pi;

  public KMPMatcher(String pattern) {
    this.pattern = pattern;
    this.pi = computePrefix(pattern);
  }

  public void search(String text) {
    int n = text.length();
    int m = pattern.length();
    int q = 0;
    for (int i = 0; i < n; i++) {
      while (q > 0 && pattern.charAt(q) != text.charAt(i))
        q = pi[q];
      if (pattern.charAt(q) == text.charAt(i))
        q = q + 1;
      // 到这里位置就是状态机在原q的状态下读入text[i],产生的新状态q
      if (q == m) {
        // found
        q = pi[q];

        System.out.print(text.substring(i + 1 - m, i + 1));
        System.out.println(" occurs in " + (i + 1 - m));
      }
    }
  }

  private int[] computePrefix(String pattern) {
    int m = pattern.length();
    int[] pi = new int[m + 1];
    pi[1] = 0;
    int k = 0;
    for (int i = 1; i < m; i++) {
      while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
        k = pi[k];
      }
      if (pattern.charAt(k) == pattern.charAt(i)) {
        k++;
      }
      pi[i + 1] = k;
    }
    return pi;
  }


  public static void main(String[] args) {
    test1();
  }

  public static void test1() {

    KMPMatcher kmp = new KMPMatcher("pattern");
    kmp.search(new In("kmp.txt").readAll());
    kmp = new KMPMatcher("aa");
    kmp.search(new In("test.txt").readAll());
  }

  public static void test(String[] args) {
    In in = new In(args[0]);
    String text = in.readAll();
    String pattern = args[1];
    KMPMatcher kmp = new KMPMatcher(pattern);
    kmp.search(text);
  }
}
